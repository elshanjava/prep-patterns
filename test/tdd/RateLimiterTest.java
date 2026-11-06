package tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

public class RateLimiterTest {

    private RateLimiter rateLimiter;
    private final AtomicLong now = new AtomicLong(0);


    @BeforeEach
    void setUp(){
        rateLimiter = new RateLimiter(2, 1000, now::get);
    }

    @Test
    void allow_underLimit_returnsTrue() {
        assertThat(rateLimiter.allow("testUserId")).isTrue();
    }

    @Test
    void allow_overLimit_returnsFalse() {
        rateLimiter.allow("testUserId");
        rateLimiter.allow("testUserId");
        assertThat(rateLimiter.allow("testUserId")).isFalse();

    }
}
