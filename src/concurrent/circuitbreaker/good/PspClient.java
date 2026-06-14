package concurrent.circuitbreaker.good;

import java.util.concurrent.atomic.AtomicInteger;

final class PspClient {
    private final CircuitBreaker cb;
    private final AtomicInteger  callCount = new AtomicInteger();
    private final int            failAfter;
    static final AtomicInteger   totalNetworkCalls = new AtomicInteger();

    PspClient(CircuitBreaker cb, int failAfter) {
        this.cb        = cb;
        this.failAfter = failAfter;
    }

    String charge(String paymentId, long amount) throws InterruptedException {
        return cb.call(() -> {
            try {
                totalNetworkCalls.incrementAndGet();
                Thread.sleep(100); // симуляция сетевого вызова
                if (callCount.incrementAndGet() > failAfter) {
                    throw new RuntimeException("PSP unavailable");
                }
                return "charged: " + paymentId;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}
