package concurrent.retry.good;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Тесты на политику повторов.
 *
 * Все четыре бага, которые здесь зафиксированы, демка показывала как успех:
 * прерывание глотала, непочинимую ошибку повторяла, jitterMs = 0 роняла на первом
 * ретрае, maxAttempts = 0 роняла NPE. Ловится это только тестом.
 */
class RetryPolicyTest {

    private static boolean isTransient(Exception e) {
        return e instanceof IOException;
    }

    // ── Повтор временных сбоев ───────────────────────────────────────────────

    @Test
    void retriesTransientFailure_untilItSucceeds() throws Exception {
        var policy   = new RetryPolicy(4, 5, 20, 0, RetryPolicyTest::isTransient);
        var attempts = new AtomicInteger();

        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() <= 2) throw new IOException("PSP timeout");
            return "charged";
        });

        assertThat(result).isEqualTo("charged");
        assertThat(attempts.get()).isEqualTo(3);
    }

    @Test
    void throwsLastFailure_whenAttemptsAreExhausted() {
        var policy   = new RetryPolicy(3, 5, 20, 0, RetryPolicyTest::isTransient);
        var attempts = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(() -> {
            throw new IOException("PSP down #" + attempts.incrementAndGet());
        })).isInstanceOf(IOException.class)
           .hasMessageContaining("#3");

        assertThat(attempts.get()).isEqualTo(3);
    }

    // ── Непочинимую ошибку НЕ повторяем ──────────────────────────────────────

    @Test
    void doesNotRetryWhenExceptionIsNotRetryable() {
        var policy   = new RetryPolicy(5, 1_000, 5_000, 0, RetryPolicyTest::isTransient);
        var attempts = new AtomicInteger();

        long start = System.currentTimeMillis();
        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new IllegalArgumentException("amount must be positive");
        })).isInstanceOf(IllegalArgumentException.class);
        long elapsed = System.currentTimeMillis() - start;

        // Одна попытка и ни одного сна: повтор 400 не станет успешным никогда,
        // а зависимости, которой плохо, достанется лишняя нагрузка.
        assertThat(attempts.get()).isEqualTo(1);
        assertThat(elapsed).isLessThan(500);
    }

    // ── Прерывание — это отмена, а не сбой ───────────────────────────────────

    @Test
    void interruptedException_isNotRetried_andRestoresFlag() {
        var policy   = new RetryPolicy(5, 50, 100, 5);
        var attempts = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new InterruptedException("операцию отменили");
        })).isInstanceOf(InterruptedException.class);

        assertThat(attempts.get()).isEqualTo(1);
        // Thread.interrupted() и проверяет, и сбрасывает — тест не пачкает соседей.
        // Без восстановления флага вызывающий об отмене не узнал бы, а следующий
        // Thread.sleep спокойно проспал бы своё.
        assertThat(Thread.interrupted()).isTrue();
    }

    // ── Краевые конфигурации ─────────────────────────────────────────────────

    @Test
    void worksWithoutJitter() throws Exception {
        var policy   = new RetryPolicy(3, 5, 20, 0, RetryPolicyTest::isTransient);
        var attempts = new AtomicInteger();

        // rng.nextLong(0) кидал IllegalArgumentException: bound must be positive
        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() == 1) throw new IOException("first fails");
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThatThrownBy(() -> new RetryPolicy(0, 10, 100, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxAttempts");

        assertThatThrownBy(() -> new RetryPolicy(3, -1, 100, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("delays");
    }

    @Test
    void delayIsCappedByMaxDelay() {
        // base 1000, но cap 20 — четыре попытки должны занять около 60ms, а не 7 секунд
        var policy = new RetryPolicy(4, 1_000, 20, 0, RetryPolicyTest::isTransient);

        long start = System.currentTimeMillis();
        assertThatThrownBy(() -> policy.execute(() -> {
            throw new IOException("always down");
        })).isInstanceOf(IOException.class);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(elapsed).isLessThan(1_000);
    }
}
