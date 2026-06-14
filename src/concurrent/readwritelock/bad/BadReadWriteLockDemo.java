package concurrent.readwritelock.bad;

import concurrent.readwritelock.model.FxRate;

import java.util.ArrayList;
import java.util.List;

public class BadReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ReadWriteLock [BAD] — synchronized блокирует всех читателей ==");

        var cache = new BadFxRateCache();
        cache.updateRate("EUR/USD", 1.08);
        cache.updateRate("GBP/USD", 1.27);

        List<Thread> readers = new ArrayList<>();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                FxRate rate = cache.getRate("EUR/USD");
                System.out.println("  [reader] EUR/USD = " + (rate != null ? rate.rate() : "null")
                        + "  concurrent=" + (BadFxRateCache.concurrentReads.get() + 1));
            });
            t.start();
            readers.add(t);
        }

        for (Thread t : readers) t.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%n10 читателей, время: %d ms%n", elapsed);
        System.out.printf("макс. одновременных читателей: %d (всегда 1 — блокируют друг друга)%n",
                BadFxRateCache.maxConcurrentReads);
        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - 10 читателей выполняются строго последовательно");
        System.out.println("  - throughput чтения не масштабируется с числом ядер");
        System.out.println("  - writer и readers в одной очереди: редкий writer блокирует частых readers");
    }
}
