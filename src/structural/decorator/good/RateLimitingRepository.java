package structural.decorator.good;

import structural.decorator.model.Account;
import java.util.concurrent.atomic.AtomicInteger;

final class RateLimitingRepository extends RepositoryDecorator {
    private final AtomicInteger counter = new AtomicInteger();
    private final int           maxRps;

    RateLimitingRepository(Repository delegate, int maxRps) {
        super(delegate);
        this.maxRps = maxRps;
    }

    @Override
    public Account find(String id) {
        if (counter.incrementAndGet() > maxRps)
            throw new RuntimeException("rate limit exceeded (" + maxRps + " rps)");
        return delegate.find(id);
    }
}
