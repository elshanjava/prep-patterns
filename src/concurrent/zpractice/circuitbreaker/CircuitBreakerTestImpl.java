package concurrent.zpractice.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

final class CircuitBreakerTestImpl {
    enum State {OPEN, CLOSE, HALF_OPEN}

    private final int failureThreshold;
    private final long recoveryTimeoutMs;

    CircuitBreakerTestImpl (int failureThreshold, long recoveryTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.recoveryTimeoutMs = recoveryTimeoutMs;
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSE);
    private final AtomicInteger failureCount = new AtomicInteger();
    private volatile long openedAt = 0;

    <T> T call(Supplier<T> action) {
        State s = state.get();
        // ОТДЕЛЬНАЯ переменная, а не s: s прочитано ДО CAS и на пути пробы равно OPEN.
        // Если передать вниз s, условие entry == HALF_OPEN не сработает никогда,
        // и после успешной пробы цепь навсегда застрянет в HALF_OPEN.
        State entry = State.CLOSE;

        if (s == State.OPEN) {
            if (System.currentTimeMillis() - openedAt < recoveryTimeoutMs) {
                throw new CircuitOpenStageException("circuit OPEN — fast-fail, no network call");
            }
            if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                System.out.println("  [circuit] → HALF_OPEN, sending probe...");
                entry = State.HALF_OPEN;
            } else {
                throw new CircuitOpenStageException("circuit OPEN — probe already in flight");
            }
        } else if (s == State.HALF_OPEN) {
            throw new CircuitOpenStageException("circuit HALF_OPEN — probe in flight");
        }

        try {
          T result = action.get();
          onSuccess(entry);
          return result;
        } catch (Throwable t) {   // не Exception: Error подвесил бы HALF_OPEN навсегда
          onFailure(entry);
          throw t;
        }
    }

    private void onSuccess(State entry) {
        failureCount.set(0);
        if (entry == CircuitBreakerTestImpl.State.HALF_OPEN && state.compareAndSet(State.HALF_OPEN, State.CLOSE)) {
            System.out.println("  [circuit] → CLOSED (probe succeeded)");
        }
    }

    private void onFailure(State entry) {
        int f = failureCount.incrementAndGet();

        if (entry == State.HALF_OPEN) {
            openedAt = System.currentTimeMillis();
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                System.out.println("  [circuit] → OPEN (probe failed)");
            }
        } else if (f >= failureThreshold) {   // >=, иначе порог 3 срабатывает на 4-й ошибке
            openedAt = System.currentTimeMillis();
            if (state.compareAndSet(State.CLOSE, State.OPEN)) {
                System.out.println("  [circuit] → OPEN after " + f + " failures");
            }
        }

    }

    CircuitBreakerTestImpl.State state() { return state.get(); }

}

