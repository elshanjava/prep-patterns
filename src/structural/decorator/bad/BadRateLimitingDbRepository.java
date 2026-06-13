package structural.decorator.bad;

import structural.decorator.model.Account;
import java.util.concurrent.atomic.AtomicInteger;

// Rate-limiting без кэша и без логирования — ещё один класс в матрице.
class BadRateLimitingDbRepository extends BadDbRepository {
    private final AtomicInteger counter  = new AtomicInteger();
    private final int           maxRps;

    BadRateLimitingDbRepository(int maxRps) { this.maxRps = maxRps; }

    @Override
    public Account find(String id) {
        if (counter.incrementAndGet() > maxRps)
            throw new RuntimeException("rate limit exceeded (" + maxRps + " rps)");
        return super.find(id);
    }
}
