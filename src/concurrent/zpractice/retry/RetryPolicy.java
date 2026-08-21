package concurrent.zpractice.retry;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

public class RetryPolicy {
    private final int  maxAttempts;
    private final long baseDelayMs;
    private final long maxDelayMs;
    private final long jitterMs;
    private final Predicate<Exception> retryable;
    private final RandomGenerator rng = RandomGenerator.getDefault();

    RetryPolicy(int maxAttempts, long baseDelayMs, long maxDelayMs, long jitterMs){
        this(maxAttempts, baseDelayMs, maxDelayMs, jitterMs, e -> true);
    }

    RetryPolicy(int maxAttempts, long baseDelayMs, long maxDelayMs, long jitterMs,
                Predicate<Exception> retryable) {
        if (maxAttempts < 1) {
            // иначе цикл не выполнится ни разу и throw last бросит null
            throw new IllegalArgumentException("maxAttempts must be >= 1, got: " + maxAttempts);
        }
        if (baseDelayMs < 0 || maxDelayMs < 0 || jitterMs < 0) {
            throw new IllegalArgumentException("delays must be >= 0");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs  = maxDelayMs;
        this.jitterMs    = jitterMs;
        this.retryable   = retryable;
    }

    private long delayFor(int attempt) {
        // Ограничиваем ПОКАЗАТЕЛЬ, а не результат. baseDelayMs * (1L << attempt) при большом
        // attempt уходит в минус, Math.min пропускает отрицательное, и Thread.sleep падает.
        // Сравнение с maxDelayMs >> shift гарантирует, что сдвиг не переполнится.
        int  shift  = Math.min(attempt, 62);
        long capped = baseDelayMs <= (maxDelayMs >> shift) ? (baseDelayMs << shift) : maxDelayMs;

        // nextLong(0) кидает IllegalArgumentException: bound must be positive.
        // Конфигурация "без джиттера" законна и падать не должна.
        return capped + (jitterMs == 0 ? 0 : rng.nextLong(jitterMs));
    }

    <T> T execute(Callable<T> action) throws Exception {
        Exception last = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw exception;
            } catch (Exception e) {
                if (!retryable.test(e)) throw e;      // не временная — отдаём сразу
                last = e;
                if (attempt == maxAttempts - 1) break;
                long delay = delayFor(attempt);
                System.out.printf("  [retry %d] waiting %dms (backoff + jitter)%n", attempt + 1, delay);
                Thread.sleep(delay);
            }
        }
        throw  last;
    }
}
