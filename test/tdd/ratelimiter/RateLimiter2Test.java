package tdd.ratelimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.ratelimiter.RateLimiter2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RateLimiter2Test {

    private AtomicLong now;
    private RateLimiter2 rateLimiter2;

    @BeforeEach
    void setUp() {
        now = new AtomicLong(0);
        rateLimiter2 = new RateLimiter2(5, 1000, now::get);
    }

    @Test
    void allow_returnsTrue_whenUnderLimit() {
        boolean allowed = rateLimiter2.allow("userId");
        assertThat(allowed).isTrue();
    }

    @Test
    void allow_returnsFalse_whenOverLimit() {
        for (int i = 0; i < 5; i++) {
             assertThat(rateLimiter2.allow("userId")).isTrue();
        }
        assertThat(rateLimiter2.allow("userId")).isFalse();
    }

    @Test
    void allow_returnsTrue_afterWindowExpired() {
        for (int i = 0; i < 5; i++) {
             assertThat(rateLimiter2.allow("userId")).isTrue();
        }
        now.set(1001);
        assertThat(rateLimiter2.allow("userId")).isTrue();
    }

    @Test
    void allow_isolatesLimitPerUser() {
        for (int i = 0; i < 5; i++) {
             assertThat(rateLimiter2.allow("user1")).isTrue();
        }
        assertThat(rateLimiter2.allow("user1")).isFalse();
        assertThat(rateLimiter2.allow("user2")).isTrue();
    }

    @Test
    void constructor_throwsWhenMaxRequestNotPositive() {
        assertThatThrownBy(()-> new RateLimiter2(0, 100, now::get))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_throwsWhenWindowMsNotPositive() {
        assertThatThrownBy(()-> new RateLimiter2(4, -100, now::get))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void allowed_threadSafe_exactlyLimitPass() throws InterruptedException {
        RateLimiter2 limiter2 = new RateLimiter2(10, 10000, now::get);

        int threads = 1000;
        var ready = new CountDownLatch(threads);
        var done = new CountDownLatch(threads);
        var allowed = new AtomicInteger(0);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                 pool.submit(()-> {
                     ready.countDown();
                     try {
                         ready.await();
                         if (limiter2.allow("user-1")) allowed.incrementAndGet();
                     } catch (InterruptedException ie) {
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
