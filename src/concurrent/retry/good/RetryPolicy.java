package concurrent.retry.good;

import java.util.concurrent.Callable;
import java.util.random.RandomGenerator;

// Exponential backoff с jitter: baseDelay * 2^attempt + random(0, jitterMs).
// Jitter разбивает синхронизацию: 1000 клиентов retryят в разные моменты — нет thundering herd.
// maxDelayMs ограничивает рост: при большом числе попыток задержка не уходит в бесконечность.
final class RetryPolicy {
    private final int  maxAttempts;
    private final long baseDelayMs;
    private final long maxDelayMs;
    private final long jitterMs;
    private final RandomGenerator rng = RandomGenerator.getDefault();

    RetryPolicy(int maxAttempts, long baseDelayMs, long maxDelayMs, long jitterMs) {
        this.maxAttempts = maxAttempts;
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs  = maxDelayMs;
        this.jitterMs    = jitterMs;
    }

    <T> T execute(Callable<T> action) throws Exception {
        Exception last = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (Exception e) {
                last = e;
                if (attempt == maxAttempts - 1) break;
                long delay = Math.min(baseDelayMs * (1L << attempt), maxDelayMs)
                           + rng.nextLong(jitterMs);
                System.out.printf("  [retry %d] waiting %dms (backoff + jitter)%n", attempt + 1, delay);
                Thread.sleep(delay);
            }
        }
        throw last;
    }
}
