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

        // ---------- ФАЗА 1: только читатели ----------
        System.out.println("\n--- фаза 1: 10 читателей одновременно ---");
        List<Thread> readers = new ArrayList<>();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                FxRate rate = cache.getRate("EUR/USD");
                System.out.println("  [reader] EUR/USD = " + (rate != null ? rate.rate() : "null"));
            });
            t.start();
            readers.add(t);
        }
        for (Thread t : readers) t.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%n10 читателей, время: %d ms%n", elapsed);
        System.out.printf("макс. одновременных читателей: %d (>1 — читают параллельно!)%n",
                FxRateCache.maxConcurrentReads.get());

        // ---------- ФАЗА 2: writer против readers ----------
        // Показываем обе стороны эксклюзивности:
        //   writer ждёт, пока отпустят активные читатели;
        //   читатели, пришедшие при занятом writeLock, ждут его.
        System.out.println("\n--- фаза 2: writer приходит, пока читатели работают ---");

        List<Thread> phase2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> cache.getRateLogged("EUR/USD"), "reader-" + (i + 1));
            t.start();
            phase2.add(t);
        }
        Thread.sleep(2);                                  // дать читателям взять readLock

        Thread writer = new Thread(() -> cache.updateRateLogged("EUR/USD", 1.09), "writer");
        writer.start();
        phase2.add(writer);

        Thread.sleep(10);                                 // теперь writer уже держит замок
        for (int i = 4; i <= 5; i++) {
            Thread t = new Thread(() -> cache.getRateLogged("EUR/USD"), "reader-" + i);
            t.start();
            phase2.add(t);
        }
        for (Thread t : phase2) t.join();

        // ---------- ФАЗА 3: апгрейд и даунгрейд ----------
        System.out.println("\n--- фаза 3: апгрейд read->write и даунгрейд write->read ---");
        boolean upgraded = cache.tryUpgradeWhileReading();
        System.out.println("  апгрейд read -> write, не отпуская read: получилось = " + upgraded
                         + "   (с lock() вместо tryLock() поток завис бы навсегда)");
        FxRate downgraded = cache.updateAndRead("EUR/USD", 1.10);
        System.out.println("  даунгрейд write -> read: разрешён, прочитали свою же запись = "
                         + downgraded.rate());

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - несколько readers работают одновременно — throughput пропорционален ядрам");
        System.out.println("  - writer ждёт только активных readers, не всю очередь (фаза 2)");
        System.out.println("  - FX-кэш: 1000 reads/sec + 1 write/sec — ideal read-heavy workload");
        System.out.println("  - апгрейд read->write невозможен, даунгрейд write->read — законный приём");
        System.out.println("  - альтернатива: ConcurrentHashMap или StampedLock (optimistic read)");
    }
}
