package concurrent.retry.good;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

public class RetryPolicy2Test {

    private static boolean isTransient(Exception e) {
        return e instanceof IOException;
    }

    @Test
    void retriesTransientFailure_untilItSucceeds() throws Exception {
        var policy = new RetryPolicy(5, 5, 10, 0, RetryPolicy2Test::isTransient);
        var retry = new AtomicInteger();

        String execute = policy.execute(() -> {
            if (retry.incrementAndGet() <= 3) {
                throw new IOException();
            }
            return "charge";
        });

        assertThat(execute).isEqualTo("charge");
        assertThat(retry.get()).isEqualTo(4);
    }

    @Test
    void throwsLastFailure_whenAttemptsAreExhausted() throws Exception {
        var policy = new RetryPolicy(7, 5, 10, 0, RetryPolicy2Test::isTransient);
        var retry = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(()-> {
            throw new IOException("PSP down #" + retry.incrementAndGet());
        }))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("#7");

        assertThat(retry.get()).isEqualTo(7);

    }

    @Test
    void doesNotRetryWhenExceptionIsNotRetryable() {
        var policy = new RetryPolicy(7, 5, 10, 0, RetryPolicy2Test::isTransient);
        var retry = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(()-> {
            throw new IllegalArgumentException("PSP down #" + retry.incrementAndGet());
        }))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("#1");

        assertThat(retry.get()).isEqualTo(1);
    }

    @Test
    void interruptedException_isNotRetried_andRestoresFlag() {
        var policy = new RetryPolicy(7, 5, 10, 0, RetryPolicy2Test::isTransient);
        var retry = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(()-> {
            throw new InterruptedException("PSP down #" + retry.incrementAndGet());
        }));

        assertThat(retry.get()).isEqualTo(1);
        assertThat(Thread.interrupted()).isEqualTo(true);
    }

    @Test
    void worksWithoutJitter() throws Exception {

        var policy = new RetryPolicy(7, 5, 10, 0, RetryPolicy2Test::isTransient);
        var retry = new AtomicInteger();

        String execute = policy.execute(() -> {
            if (retry.incrementAndGet() == 1) throw new IOException("first fails");
            return "ok";
        });

        assertThat(execute).isEqualTo("ok");
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThatThrownBy(()-> new RetryPolicy(0, 5, 10, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxAttempts");

        assertThatThrownBy(()-> new RetryPolicy(5, -5, 10, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("delays");
    }

    @Test
    void delayIsCappedByMaxDelay() {
        var policy = new RetryPolicy(4, 1_000, 20, 0, RetryPolicy2Test::isTransient);

        long start = System.currentTimeMillis();
        assertThatThrownBy(()-> policy.execute(()-> {
            throw new IOException("always down");
        }))
        .isInstanceOf(IOException.class);

        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed).isLessThan(1000);
    }

}
















