package structural.proxy.good;

import structural.proxy.model.Account;
import java.util.concurrent.atomic.AtomicInteger;

// Третий слой: rate-limiting изолирован, тестируется с mock-делегатом.
// Порядок слоёв: Security → RateLimit → Cache → Real — задаётся в runtime.
final class RateLimitingProxy implements AccountService {
    private final AccountService  delegate;
    private final AtomicInteger   counter = new AtomicInteger();
    private final int             maxRps;

    RateLimitingProxy(AccountService delegate, int maxRps) {
        this.delegate = delegate;
        this.maxRps   = maxRps;
    }

    @Override
    public Account get(String id) {
        if (counter.incrementAndGet() > maxRps)
            throw new RuntimeException("rate limit exceeded (" + maxRps + " rps)");
        return delegate.get(id);
    }
}
