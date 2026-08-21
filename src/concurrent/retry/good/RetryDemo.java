package concurrent.retry.good;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== Retry [GOOD] — exponential backoff с jitter ==");

        transientFailures();
        permanentFailure();

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - jitter: 1000 клиентов retryят в разные моменты — нет thundering herd");
        System.out.println("  - exponential: каждая волна нагрузки вдвое меньше предыдущей");
        System.out.println("  - maxDelayMs: задержка не растёт бесконечно (cap = 1000ms)");
        System.out.println("  - предикат: временную ошибку повторяем, 400 отдаём сразу");
        System.out.println("  - прерывание пробрасывается с восстановленным флагом:");
        System.out.println("    отмену операции нельзя перепутать со сбоем PSP");
        System.out.println("  - ретрай ТРЕБУЕТ идемпотентности: ответ мог потеряться уже после");
        System.out.println("    списания, и повтор спишет второй раз (см. tdd/idempotency)");
        System.out.println("  - в продакшне: Resilience4j @Retry с retryExceptions + дедлайном");
    }

    // ── Временные сбои: повторяем, и на 4-й раз получается ───────────────────
    private static void transientFailures() throws Exception {
        System.out.println();
        System.out.println("--- временный сбой PSP: повторяем ---");

        var policy   = new RetryPolicy(4, 100, 1000, 50, RetryDemo::isTransient);
        var attempts = new AtomicInteger();

        long start = System.currentTimeMillis();
        String result = policy.execute(() -> {
            int n = attempts.incrementAndGet();
            if (n <= 3) throw new IOException("PSP timeout (attempt " + n + ")");
            return "charged: pay-1";
        });
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("  result: " + result);
        System.out.printf("  попыток: %d, время: %d ms%n", attempts.get(), elapsed);
    }

    // ── Постоянная ошибка: НЕ повторяем ──────────────────────────────────────
    private static void permanentFailure() {
        System.out.println();
        System.out.println("--- ошибка валидации: повторять бессмысленно ---");

        var policy   = new RetryPolicy(4, 100, 1000, 50, RetryDemo::isTransient);
        var attempts = new AtomicInteger();

        long start = System.currentTimeMillis();
        try {
            policy.execute(() -> {
                attempts.incrementAndGet();
                throw new IllegalArgumentException("amount must be positive");
            });
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("  failed: " + e.getMessage());
            System.out.printf("  попыток: %d, время: %d ms — отдали сразу, без сна%n",
                    attempts.get(), elapsed);
        }
    }

    // Временное — то, что может пройти при повторе: сеть, таймаут, 503.
    // Всё остальное (валидация, 400, ошибка в аргументах) повтором не лечится.
    private static boolean isTransient(Exception e) {
        return e instanceof IOException;
    }
}
