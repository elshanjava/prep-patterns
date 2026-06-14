package concurrent.readwritelock.good;

import concurrent.readwritelock.model.FxRate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// ReentrantReadWriteLock: несколько читателей одновременно, один writer эксклюзивно.
// FX-кэш читается тысячи раз в секунду, обновляется раз в секунду — идеальный кейс.
// ReadLock не мешает другим ReadLock; WriteLock ждёт завершения всех ReadLock.
final class FxRateCache {
    private final Map<String, FxRate>  cache = new HashMap<>();
    private final ReentrantReadWriteLock rwl  = new ReentrantReadWriteLock();
    static final AtomicInteger concurrentReads  = new AtomicInteger();
    static volatile int        maxConcurrentReads = 0;

    FxRate getRate(String pair) {
        rwl.readLock().lock();
        try {
            int n = concurrentReads.incrementAndGet();
            maxConcurrentReads = Math.max(maxConcurrentReads, n);
            Thread.sleep(5); // симуляция чтения
            return cache.get(pair);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            concurrentReads.decrementAndGet();
            rwl.readLock().unlock();
        }
    }

    void updateRate(String pair, double rate) {
        rwl.writeLock().lock();
        try {
            cache.put(pair, new FxRate(pair, rate, Instant.now()));
            System.out.println("  [writer] updated " + pair + " = " + rate);
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
