package concurrent.readwritelock.good;

import concurrent.readwritelock.model.FxRate;

import java.util.ArrayList;
import java.util.List;

public class ReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ReadWriteLock [GOOD] — читатели параллельны, writer эксклюзивен ==");

        var cache = new FxRateCache();
        cache.updateRate("EUR/USD", 1.08);
        cache.updateRate("GBP/USD", 1.27);

        List<Thread> readers = new ArrayList<>();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                FxRate rate = cache.getRate("EUR/USD");
                System.out.println("  [reader] EUR/USD = " + (rate != null ? rate.rate() : "null")
                        + "  concurrent=" + FxRateCache.concurrentReads.get());
            });
            t.start();
            readers.add(t);
        }

        for (Thread t : readers) t.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%n10 читателей, время: %d ms%n", elapsed);
        System.out.printf("макс. одновременных читателей: %d (>1 — читают параллельно!)%n",
                FxRateCache.maxConcurrentReads);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - несколько readers работают одновременно — throughput пропорционален ядрам");
        System.out.println("  - writer ждёт только активных readers, не всю очередь");
        System.out.println("  - FX-кэш: 1000 reads/sec + 1 write/sec — ideal read-heavy workload");
        System.out.println("  - альтернатива: ConcurrentHashMap или StampedLock (optimistic read)");
    }
}
