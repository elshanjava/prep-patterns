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

        if (s == State.OPEN) {
            if (System.currentTimeMillis() - openedAt < recoveryTimeoutMs) {
                throw new CircuitOpenException("circuit OPEN — fast-fail, no network call");
            }
            // только ОДИН поток выигрывает переход OPEN → HALF_OPEN и шлёт пробу
            if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                System.out.println("  [circuit] → HALF_OPEN, sending probe...");
            } else {
                throw new CircuitOpenException("circuit OPEN — probe already in flight");
            }
        } else if (s == State.HALF_OPEN) {
            // проба уже летит у другого потока — второй не пускаем
            throw new CircuitOpenException("circuit HALF_OPEN — probe in flight");
        }

        try {
            T result = action.get();   // вне лока — сетевые вызовы идут параллельно
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }

    private void onSuccess() {
        failures.set(0);
        // проба удалась → замыкаемся (только если реально были в HALF_OPEN)
        if (state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
            System.out.println("  [circuit] → CLOSED (probe succeeded)");
        }
    }

    private void onFailure() {
        int f = failures.incrementAndGet();
        State s = state.get();

        if (s == State.HALF_OPEN) {
            openedAt = System.currentTimeMillis();          // openedAt ДО смены state
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                System.out.println("  [circuit] → OPEN (probe failed)");
            }
        } else if (f >= failureThreshold && s == State.CLOSED) {
            openedAt = System.currentTimeMillis();
            if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                System.out.println("  [circuit] → OPEN after " + f + " failures");
            }
        }
    }

    State state() { return state.get(); }
}
