package concurrent.zpractice.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;

public class PspClientTestImpl {
    private final CircuitBreakerTestImpl circuitBreakerDemo;
    private final AtomicInteger callCount = new AtomicInteger();
    private final int failAfter;
    private final long healAt;
    static final AtomicInteger totalNetworkCalls = new AtomicInteger();

    PspClientTestImpl(CircuitBreakerTestImpl circuitBreakerDemo, int failAfter, long healAfterMs) {
        this.circuitBreakerDemo = circuitBreakerDemo;
        this.failAfter = failAfter;
        this.healAt = System.currentTimeMillis() + healAfterMs;
    }

    String charge(String paymentId, long amount) {
        return circuitBreakerDemo.call(() -> {
            try {
                totalNetworkCalls.incrementAndGet();
                Thread.sleep(100); // симуляция сетевого вызова
                // PSP лежит только до healAt — иначе проба в HALF_OPEN никогда бы не прошла
                boolean down = callCount.incrementAndGet() > failAfter
                        && System.currentTimeMillis() < healAt;
                if (down) {
                    throw new RuntimeException("PSP unavailable");
                }
                return "charged: " + paymentId;
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}
