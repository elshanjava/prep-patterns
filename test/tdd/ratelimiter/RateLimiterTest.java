package tdd.ratelimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Sliding Window Rate Limiter — TDD kata (Revolut Coding Exercise Q137).
 *
 * "Allow 100 requests per second per merchant. Must work correctly."
 *
 *   1. первый запрос разрешён
 *   2. запросы в рамках лимита разрешены
 *   3. превышение лимита — отклонён
 *   4. после истечения окна — снова разрешены
 *   5. скользящее окно (не фиксированное)
 *   6. разные пользователи — независимые счётчики
 *   7. thread-safe — точный подсчёт под нагрузкой
 */
class RateLimiterTest {

    private RateLimiter limiter;

    @BeforeEach
    void setUp() {
        // 5 запросов в 1000ms — удобно для тестов
        limiter = new RateLimiter(5, 1000);
    }

    // ── Шаг 1: первый запрос разрешён ────────────────────────────────────────

    @Test
    void allow_firstRequestIsAllowed() {
        assertThat(limiter.allow("merchant-1")).isTrue();
    }

    // ── Шаг 2: запросы в рамках лимита разрешены ─────────────────────────────

    @Test
    void allow_requestsWithinLimitAreAllowed() {
        for (int i = 0; i < 5; i++) {
            assertThat(limiter.allow("merchant-1")).isTrue();
        }
    }

    // ── Шаг 3: превышение лимита — отклонён ──────────────────────────────────

    @Test
    void allow_requestsExceedingLimitAreDenied() {
        for (int i = 0; i < 5; i++) {
            limiter.allow("merchant-1");
        }

        assertThat(limiter.allow("merchant-1")).isFalse();
    }

    // ── Шаг 4: после истечения окна — снова разрешены ────────────────────────

    @Test
    void allow_allowsAgainAfterWindowExpires() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            limiter.allow("merchant-1");
        }
        assertThat(limiter.allow("merchant-1")).isFalse();

        Thread.sleep(1100); // ждём истечения окна

        assertThat(limiter.allow("merchant-1")).isTrue();
    }

    // ── Шаг 5: скользящее окно — не фиксированное ────────────────────────────

    @Test
    void allow_slidingWindow_notFixed() throws InterruptedException {
        // лимит: 5 за 1000ms
        // t=0ms:   3 запроса
        // t=600ms: 3 запроса (всего 6 в [0..1600], но в окне [600..1600] только 3)
        limiter.allow("merchant-1"); // t≈0
        limiter.allow("merchant-1"); // t≈0
        limiter.allow("merchant-1"); // t≈0

        Thread.sleep(600);

        // первые 3 уже "уходят" из окна [600, 1600]
        assertThat(limiter.allow("merchant-1")).isTrue();  // 4-й в окне
        assertThat(limiter.allow("merchant-1")).isTrue();  // 5-й в окне
        // 6-й: первые 3 ещё в окне [0..600 < 1000], всё ещё 5 в окне
        assertThat(limiter.allow("merchant-1")).isFalse();
    }

    // ── Шаг 6: разные пользователи — независимые лимиты ─────────────────────

    @Test
    void allow_differentUsersHaveIndependentLimits() {
        for (int i = 0; i < 5; i++) {
            limiter.allow("merchant-1");
        }

        // merchant-1 исчерпал лимит — merchant-2 не должен пострадать
        assertThat(limiter.allow("merchant-1")).isFalse();
        assertThat(limiter.allow("merchant-2")).isTrue();
    }

    // ── Шаг 7: thread-safe — точный подсчёт ──────────────────────────────────

    @Test
    void allow_threadSafe_exactlyLimitRequestsPass() throws InterruptedException {
        RateLimiter strictLimiter = new RateLimiter(10, 60_000); // 10 за минуту
        int threads = 50;
        AtomicInteger allowed = new AtomicInteger(0);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        if (strictLimiter.allow("merchant-1")) allowed.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        assertThat(allowed.get()).isEqualTo(10);
    }
}
