package tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UrlShortenerTest {

    private UrlShortener shortener;

    @BeforeEach
    void setUp() {
        shortener = new UrlShortener();
    }

    @Test
    void shortenThenResolve_returnOriginalUrl() {
        String shorten = shortener.shorten("www.test.com");
        assertThat(shortener.resolve(shorten)).isEqualTo("www.test.com");
    }

    @Test
    void shorten_sameUrl_sameCode() {
        String first = shortener.shorten("www.test.com");
        String second = shortener.shorten("www.test.com");
        assertThat(first).isEqualTo(second);
    }

    @Test
    void resolve_throwsWhenCodeUnknown() {
        assertThatThrownBy(()-> shortener.resolve("testcode"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shorten_throwsWhenUrlIsNull() {
        assertThatThrownBy(()-> shortener.shorten(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shorten_threadSafe_sameUrlGetsSameCode() throws InterruptedException {
        int threads = 50;
        String url = "www.test.com";

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int round = 0; round < 200; round++) {
                UrlShortener fresh = new UrlShortener();                       // свежий каждый раунд
                Set<String> codes = ConcurrentHashMap.newKeySet();             // потокобезопасный сток
                var ready = new CountDownLatch(threads);
                var done = new CountDownLatch(threads);
                for (int i = 0; i < 100; i++) {
                    pool.submit(()-> {
                        ready.countDown();
                        try {
                            ready.await();
                            codes.add(fresh.shorten(url));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            done.countDown();
                        }
                    });

                }

                assertThat(done.await(100, TimeUnit.MILLISECONDS)).isTrue();
                assertThat(codes).hasSize(1);
            }

        }

    }
}
