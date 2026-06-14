package concurrent.retry.bad;

import java.util.concurrent.atomic.AtomicInteger;

// Фиксированная задержка между попытками.
// Thundering herd: если 1000 клиентов упали одновременно — они ВСЕ повторят через ровно 100ms.
// Вторая волна нагрузки убивает PSP снова в тот же момент.
// Нет jitter → синхронизированные retry = пики нагрузки периодически повторяются.
final class BadPspClient {
    private static final long RETRY_DELAY_MS = 100;
    private static final int  MAX_ATTEMPTS   = 3;
    final AtomicInteger attempts = new AtomicInteger();

    private final int failUntil;
    BadPspClient(int failUntil) { this.failUntil = failUntil; }

    String charge(String paymentId, long amount) throws InterruptedException {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {
                attempts.incrementAndGet();
                if (attempts.get() <= failUntil) throw new RuntimeException("PSP error");
                return "charged: " + paymentId;
            } catch (RuntimeException e) {
                if (i == MAX_ATTEMPTS - 1) throw e;
                System.out.printf("  [retry %d] waiting %dms (fixed delay)%n", i + 1, RETRY_DELAY_MS);
                Thread.sleep(RETRY_DELAY_MS); // всегда ровно 100ms — thundering herd!
            }
        }
        throw new RuntimeException("unreachable");
    }
}
