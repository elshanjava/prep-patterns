package concurrent.retry.good;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

// Exponential backoff с jitter: baseDelay * 2^attempt + random(0, jitterMs), сверху cap.
// Jitter разбивает синхронизацию: 1000 клиентов retryят в разные моменты — нет thundering herd.
//
// ЧТО ПОВТОРЯТЬ — половина паттерна, а не довесок. Повторять имеет смысл только временные
// сбои: таймаут, 503, обрыв соединения. Повтор 400 или ошибки валидации не станет успешным
// никогда и лишь добавит нагрузки зависимости, которой и так плохо. Отсюда retryable.
//
// ЧЕГО ЗДЕСЬ СОЗНАТЕЛЬНО НЕТ: общего дедлайна. maxAttempts ограничивает ЧИСЛО попыток,
// но не суммарное время — при cap = 1000ms и 10 попытках это до 10 секунд, а исходный
// HTTP-запрос к тому моменту давно отвалится по своему таймауту. В проде политика
// принимает ещё и крайний срок и обрезает по нему сон. Опущено ради читаемости.
//
// РЕТРАЙ ТРЕБУЕТ ИДЕМПОТЕНТНОСТИ. Ответ PSP мог потеряться уже ПОСЛЕ того, как деньги
// ушли — повтор спишет второй раз. Поэтому повторяемая операция обязана нести ключ
// идемпотентности (см. tdd/idempotency). Без него ретрай не отказоустойчивость, а баг.
final class RetryPolicy {
    private final int  maxAttempts;
    private final long baseDelayMs;
    private final long maxDelayMs;
    private final long jitterMs;
    private final Predicate<Exception> retryable;
    private final RandomGenerator rng = RandomGenerator.getDefault();

    /** Повторяет любую ошибку — годится, только если вызывающий уверен, что все они временные. */
    RetryPolicy(int maxAttempts, long baseDelayMs, long maxDelayMs, long jitterMs) {
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

    <T> T execute(Callable<T> action) throws Exception {
        Exception last = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (InterruptedException e) {
                // Прерывание — это ОТМЕНА, а не сбой зависимости: повторять нечего.
                // Выброс исключения уже сбросил флаг, восстанавливаем — иначе вызывающий
                // не узнает об отмене, а следующий Thread.sleep спокойно проспит своё.
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                if (!retryable.test(e)) throw e;      // не временная — отдаём сразу
                last = e;
                if (attempt == maxAttempts - 1) break;
                long delay = delayFor(attempt);
                System.out.printf("  [retry %d] waiting %dms (backoff + jitter)%n", attempt + 1, delay);
                Thread.sleep(delay);
            }
        }
        throw last;
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
}
