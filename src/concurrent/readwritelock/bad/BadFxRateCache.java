package concurrent.readwritelock.bad;

import concurrent.readwritelock.model.FxRate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// Весь кэш под одним synchronized — читатели блокируют друг друга.
// 10 потоков читают EUR/USD одновременно? Выполняются строго по одному.
// Throughput чтения = throughput одного потока, хотя читать безопасно конкурентно.
final class BadFxRateCache {
    private final Map<String, FxRate> cache = new HashMap<>();
    static final AtomicInteger concurrentReads = new AtomicInteger();
    static volatile int maxConcurrentReads = 0;

    synchronized FxRate getRate(String pair) {
        int n = concurrentReads.incrementAndGet();
        maxConcurrentReads = Math.max(maxConcurrentReads, n); // в реальности всегда 1
        try {
            Thread.sleep(5); // симуляция чтения
            return cache.get(pair);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            concurrentReads.decrementAndGet();
        }
    }

    synchronized void updateRate(String pair, double rate) {
        cache.put(pair, new FxRate(pair, rate, Instant.now()));
        System.out.println("  [writer] updated " + pair + " = " + rate);
    }
}
