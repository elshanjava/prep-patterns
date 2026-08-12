package concurrent.circuitbreaker.good;

import java.util.concurrent.atomic.AtomicInteger;

final class PspClient {
    private final CircuitBreaker cb;
    private final AtomicInteger  callCount = new AtomicInteger();
    private final int            failAfter;
    private final long           healAt;      // момент, когда PSP выздоравливает
    static final AtomicInteger   totalNetworkCalls = new AtomicInteger();

    PspClient(CircuitBreaker cb, int failAfter, long healAfterMs) {
        this.cb        = cb;
        this.failAfter = failAfter;
        this.healAt    = System.currentTimeMillis() + healAfterMs;
    }

    // throws InterruptedException тут не нужен: Supplier.get() не объявляет checked,
    // поэтому лямбда обязана погасить InterruptedException сама — что она ниже и делает.
    String charge(String paymentId, long amount) {
        return cb.call(() -> {
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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}
