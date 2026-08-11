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

        if (s == State.OPEN) {
            if (System.currentTimeMillis() - openedAt < recoveryTimeoutMs) {
                throw new CircuitOpenStageException("circuit OPEN — fast-fail, no network call");
            }
            if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                System.out.println("  [circuit] → HALF_OPEN, sending probe...");
            } else {
                throw new CircuitOpenStageException("circuit OPEN — probe already in flight");
            }
        } else if (s == State.HALF_OPEN) {
            throw new CircuitOpenStageException("circuit HALF_OPEN — probe in flight");
        }

        try {
          T result = action.get();
//          onSuccess();
            return result;
        } catch (Exception e) {
//          onFailure();
            throw e;
        }
    }

    CircuitBreakerTestImpl.State state() { return state.get(); }

}

