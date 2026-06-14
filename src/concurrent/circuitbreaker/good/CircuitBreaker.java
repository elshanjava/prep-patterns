package concurrent.circuitbreaker.good;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

// CLOSED → (failureThreshold превышен) → OPEN → (recoveryTimeout истёк) → HALF_OPEN
// HALF_OPEN → (успех) → CLOSED | (неудача) → OPEN
final class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private final int    failureThreshold;
    private final long   recoveryTimeoutMs;

    private volatile State        state     = State.CLOSED;
    private volatile long         openedAt  = 0;
    private final AtomicInteger   failures  = new AtomicInteger();

    CircuitBreaker(int failureThreshold, long recoveryTimeoutMs) {
        this.failureThreshold  = failureThreshold;
        this.recoveryTimeoutMs = recoveryTimeoutMs;
    }

    <T> T call(Supplier<T> action) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - openedAt < recoveryTimeoutMs) {
                throw new CircuitOpenException("circuit OPEN — fast-fail, no network call");
            }
            state = State.HALF_OPEN;
            System.out.println("  [circuit] → HALF_OPEN, sending probe...");
        }

        try {
            T result = action.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }

    private void onSuccess() {
        failures.set(0);
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            System.out.println("  [circuit] → CLOSED (probe succeeded)");
        }
    }

    private void onFailure() {
        int f = failures.incrementAndGet();
        if (f >= failureThreshold && state == State.CLOSED) {
            state    = State.OPEN;
            openedAt = System.currentTimeMillis();
            System.out.println("  [circuit] → OPEN after " + f + " failures");
        } else if (state == State.HALF_OPEN) {
            state    = State.OPEN;
            openedAt = System.currentTimeMillis();
            System.out.println("  [circuit] → OPEN (probe failed)");
        }
    }

    State state() { return state; }
}
