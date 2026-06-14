package concurrent.retry.good;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== Retry [GOOD] — exponential backoff с jitter ==");

        var policy  = new RetryPolicy(4, 100, 1000, 50);
        var attempts = new AtomicInteger();
        int failUntil = 3;

        long start = System.currentTimeMillis();
        try {
            String result = policy.execute(() -> {
                int n = attempts.incrementAndGet();
                if (n <= failUntil) throw new RuntimeException("PSP error (attempt " + n + ")");
                return "charged: pay-1";
            });
            System.out.println("  result: " + result);
        } catch (Exception e) {
            System.out.println("  failed: " + e.getMessage());
        }
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%nпопыток: %d, время: %d ms%n", attempts.get(), elapsed);

        System.out.println();
        System.out.println("Задержки (примерные):");
        System.out.println("  attempt 1 → fail → wait ~100ms + jitter(0..50)");
        System.out.println("  attempt 2 → fail → wait ~200ms + jitter(0..50)");
        System.out.println("  attempt 3 → fail → wait ~400ms + jitter(0..50)");
        System.out.println("  attempt 4 → success");

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - jitter: 1000 клиентов retryят в разные моменты — нет thundering herd");
        System.out.println("  - exponential: каждая волна нагрузки вдвое меньше предыдущей");
        System.out.println("  - maxDelayMs: задержка не растёт бесконечно (cap = 1000ms)");
        System.out.println("  - в продакшне: Resilience4j @Retry с exponentialBackoff — тот же алгоритм");
    }
}
