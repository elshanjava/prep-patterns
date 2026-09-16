package tdd.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.urlshortener.NotFoundException;
import tdd.zpractice.urlshortener.UrlShortener2;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UrlShortener2Test {
    private UrlShortener2 urlShortener;

    @BeforeEach
    void setUp() {
        urlShortener = new UrlShortener2();
    }

    @Test
    void shorten_thenResolve_returnsOriginalUrl() {
        String shorten = urlShortener.shorten("https://revolut.com/payments");
        String resolve = urlShortener.resolve(shorten);

        assertThat(shorten).isNotNull();
        assertThat(resolve).isNotNull();
        assertThat(resolve).isEqualTo("https://revolut.com/payments");
    }

    @Test
    void shorten_sameUrl_returnsSameCode() {
        String first  = urlShortener.shorten("https://revolut.com/payments");
        String second = urlShortener.shorten("https://revolut.com/payments");

        assertThat(second).isEqualTo(first);
    }

    @Test
    void shorten_differentUrls_returnDifferentCodes() {
        String first  = urlShortener.shorten("https://revolut.com/payments");
        String second = urlShortener.shorten("https://revolut.com/payments1");
        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void shorten_returnsNonEmptyCodeShorterThanUrl() {
        String code  = urlShortener.shorten("https://revolut.com/payments");
        String url  = urlShortener.resolve(code);

        assertThat(code).isNotNull();
        assertThat(code).isNotEmpty();
        assertThat(code.length()).isLessThan(url.length());

    }

    @Test
    void resolve_throwsWhenCodeIsUnknown() {
        assertThatThrownBy(()-> urlShortener.resolve("test"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shorten_threadSafe_sameUrlGetsSameCode() throws InterruptedException {
        String url = "https://revolut.com/payments";
        var shortener = new UrlShortener2();
        int threads = 100;
        var ready = new CountDownLatch(threads);
        var done = new CountDownLatch(threads);
        Set<String> codes = ConcurrentHashMap.newKeySet();

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                 pool.submit(()-> {
                     ready.countDown();
                     try {
                         ready.await();
                         codes.add(shortener.shorten(url));
                     } catch (InterruptedException ie) {
                         Thread.currentThread().interrupt();
                     } finally {
                         done.countDown();
                     }
                 });
            }
            assertThat(done.await(100, TimeUnit.MILLISECONDS)).isTrue();
        }
        assertThat(codes).hasSize(1);

    }

}
