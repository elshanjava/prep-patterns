package tdd.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * URL Shortener — TDD kata (Revolut Live Coding, упоминается в ноябре 2024).
 *
 *   1. shorten возвращает непустой код
 *   2. resolve возвращает исходный URL
 *   3. та же URL → тот же код (идемпотентность)
 *   4. разные URL → разные коды
 *   5. неизвестный код → исключение
 *   6. код короче исходного URL
 *   7. thread-safe — нет дубликатов под конкурентной нагрузкой
 */
class UrlShortenerTest {

    private UrlShortener shortener;

    @BeforeEach
    void setUp() {
        shortener = new UrlShortener();
    }

    // ── Шаг 1: shorten возвращает непустой код ───────────────────────────────

    @Test
    void shorten_returnsNonBlankCode() {
        String code = shortener.shorten("https://revolut.com/payments");

        assertThat(code).isNotBlank();
    }

    // ── Шаг 2: resolve возвращает исходный URL ───────────────────────────────

    @Test
    void resolve_returnsOriginalUrl() {
        String url  = "https://revolut.com/payments";
        String code = shortener.shorten(url);

        assertThat(shortener.resolve(code)).isEqualTo(url);
    }

    // ── Шаг 3: та же URL → тот же код (идемпотентность) ─────────────────────

    @Test
    void shorten_sameUrlReturnsSameCode() {
        String url = "https://revolut.com/payments";

        assertThat(shortener.shorten(url)).isEqualTo(shortener.shorten(url));
    }

    // ── Шаг 4: разные URL → разные коды ──────────────────────────────────────

    @Test
    void shorten_differentUrlsReturnDifferentCodes() {
        String code1 = shortener.shorten("https://revolut.com/payments");
        String code2 = shortener.shorten("https://revolut.com/accounts");

        assertThat(code1).isNotEqualTo(code2);
    }

    // ── Шаг 5: неизвестный код → исключение ──────────────────────────────────

    @Test
    void resolve_throwsForUnknownCode() {
        assertThatThrownBy(() -> shortener.resolve("unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }

    // ── Шаг 6: код короче исходного URL ──────────────────────────────────────

    @Test
    void shorten_codeIsShorterThanUrl() {
        String url  = "https://revolut.com/very/long/payment/url/with/many/params?id=123&ref=456";
        String code = shortener.shorten(url);

        assertThat(code.length()).isLessThan(url.length());
    }

    // ── Шаг 7: thread-safe — нет дублирования кодов под нагрузкой ───────────

    @Test
    void shorten_threadSafe_sameUrlGetsSameCode() throws InterruptedException {
        String url = "https://revolut.com/payments";
        int threads = 50;
        Set<String> codes = ConcurrentHashMap.newKeySet();
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        codes.add(shortener.shorten(url));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        // одна URL → ровно один уникальный код, независимо от количества потоков
        assertThat(codes).hasSize(1);
    }
}
