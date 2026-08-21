package concurrent.zpractice.readwritelock;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheService {
    private final Map<String, FxRate> cache = new HashMap<>();
    private final ReadWriteLock rwl = new ReentrantReadWriteLock(true);

    FxRate getRate(String pair) {
        rwl.readLock().lock();
        try {
            return cache.get(pair);
        } finally {
            rwl.readLock().unlock();
        }
    }

    void updateRate(String pair, double rate) {
        rwl.writeLock().lock();
        try {
            cache.put(pair, new FxRate(pair, rate, Instant.now()));
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
