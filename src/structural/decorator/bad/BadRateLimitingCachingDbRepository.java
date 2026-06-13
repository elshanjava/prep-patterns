package structural.decorator.bad;

import structural.decorator.model.Account;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Rate-limit + Cache: понадобился ещё один класс.
// Уже 6 классов для 3 поведений. Добавить Circuit Breaker → 2^4 = 16 комбинаций.
class BadRateLimitingCachingDbRepository extends BadDbRepository {
    private final Map<String, Account> cache   = new ConcurrentHashMap<>();
    private final AtomicInteger        counter = new AtomicInteger();
    private final int                  maxRps;

    BadRateLimitingCachingDbRepository(int maxRps) { this.maxRps = maxRps; }

    @Override
    public Account find(String id) {
        if (counter.incrementAndGet() > maxRps)
            throw new RuntimeException("rate limit exceeded");
        return cache.computeIfAbsent(id, super::find);
    }
}
