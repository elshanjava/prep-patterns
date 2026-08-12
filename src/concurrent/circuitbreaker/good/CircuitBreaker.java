package concurrent.circuitbreaker.good;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// CLOSED → (failureThreshold превышен) → OPEN → (recoveryTimeout истёк) → HALF_OPEN
// HALF_OPEN → (успех) → CLOSED | (неудача) → OPEN
//
// Потокобезопасность: переходы состояния идут через compareAndSet, поэтому
// пробу в HALF_OPEN посылает ровно один поток; action.get() выполняется вне
// любого лока, так что сетевые вызовы остаются параллельными.
final class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private final int    failureThreshold;
    private final long   recoveryTimeoutMs;

    private final AtomicReference<State> state    = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger          failures = new AtomicInteger();
    private volatile long                openedAt = 0;

    CircuitBreaker(int failureThreshold, long recoveryTimeoutMs) {
        this.failureThreshold  = failureThreshold;
        this.recoveryTimeoutMs = recoveryTimeoutMs;
    }

    <T> T call(Supplier<T> action) {
        State s = state.get();
        // В каком качестве идёт ЭТОТ вызов: обычный трафик (CLOSED) или проба (HALF_OPEN).
        // Решение по его результату принимаем исходя из entry, а не из текущего state:
        // пока вызов висит в сети, состояние могло уехать, и чужой поздний ответ
        // не должен ни закрывать цепь, ни ронять чужую пробу.
        State entry = State.CLOSED;

        if (s == State.OPEN) {
            if (System.currentTimeMillis() - openedAt < recoveryTimeoutMs) {
                throw new CircuitOpenException("circuit OPEN — fast-fail, no network call");
            }
            // только ОДИН поток выигрывает переход OPEN → HALF_OPEN и шлёт пробу
            if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                System.out.println("  [circuit] → HALF_OPEN, sending probe...");
                entry = State.HALF_OPEN;
            } else {
                throw new CircuitOpenException("circuit OPEN — probe already in flight");
            }
        } else if (s == State.HALF_OPEN) {
            // проба уже летит у другого потока — второй не пускаем
            throw new CircuitOpenException("circuit HALF_OPEN — probe in flight");
        }

        // catch(Throwable), а не catch(Exception): если из action вылетит Error,
        // состояние осталось бы HALF_OPEN навсегда и цепь больше никогда не открылась бы —
        // все последующие вызовы вечно получали бы "probe in flight".
        try {
            T result = action.get();   // вне лока — сетевые вызовы идут параллельно
            onSuccess(entry);
            return result;
        } catch (Throwable t) {
            onFailure(entry);
            throw t;
        }
    }

    private void onSuccess(State entry) {
        failures.set(0);
        // замыкаемся только если этот вызов И БЫЛ пробой
        if (entry == State.HALF_OPEN && state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
            System.out.println("  [circuit] → CLOSED (probe succeeded)");
        }
    }

    private void onFailure(State entry) {
        int f = failures.incrementAndGet();

        if (entry == State.HALF_OPEN) {
            openedAt = System.currentTimeMillis();          // openedAt ДО смены state
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                System.out.println("  [circuit] → OPEN (probe failed)");
            }
        } else if (f >= failureThreshold) {
            openedAt = System.currentTimeMillis();
            // CAS сам отсеет случай, когда цепь уже не CLOSED
            if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                System.out.println("  [circuit] → OPEN after " + f + " failures");
            }
        }
    }

    State state() { return state.get(); }
}
