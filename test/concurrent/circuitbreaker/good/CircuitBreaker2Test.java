package concurrent.circuitbreaker.good;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CircuitBreaker2Test {

    @Test
    void startsClosed() {
        var cb = new CircuitBreaker(3, 50);

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void staysClosedWhileFailuresBelowThreshold() {
        var cb = new CircuitBreaker(3, 50);

        failureTwice(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void opensWhenThresholdReached() {
        var cb = new CircuitBreaker(3, 50);
        failureTwice(cb);
        expectFailure(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void successResetsFailureCounter() {
        var cb = new CircuitBreaker(3, 50);
        failureTwice(cb);
        cb.call(()-> "ok");
        failureTwice(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void whenOpen_failsFastWithoutInvokingAction() {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);

        var invocation = new AtomicInteger();

        assertThatThrownBy(()-> cb.call(()->{
            invocation.incrementAndGet();
            return "should not execute";
        })).isInstanceOf(CircuitOpenException.class);

        assertThat(invocation.get()).isZero();
    }

    @Test
    void afterRecoveryTimeout_probeSucceeds_closesCircuit() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        Thread.sleep(80);

        assertThat(cb.call(()-> "ok")).isEqualTo("ok");
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void afterRecoveryTimeout_probeFails_reopensCircuit() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        Thread.sleep(80);
        expectFailure(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void inHalfOpen_onlyOneProbeIsAllowed_othersFailFast() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        Thread.sleep(80);

        int threads = 8;

        var invocations = new AtomicInteger();
        var rejections  = new AtomicInteger();
        var probeStarted = new CountDownLatch(1);
        var releaseProbe = new CountDownLatch(1);
        // Ждём, пока ВСЕ проигравшие получат отказ. Детерминированно: они не блокируются.
        var allRejected  = new CountDownLatch(threads - 1);

        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < threads; i++) {
                 pool.submit(()-> {
                     try {
                         cb.call(()-> {
                             invocations.incrementAndGet();
                             probeStarted.countDown();
                             try {
                                 releaseProbe.await();
                             } catch (InterruptedException e) {
                                 Thread.currentThread().interrupt();
                             }
                             return "probe ok";
                         });
                     } catch (CircuitOpenException e) {
                         rejections.incrementAndGet();
                         allRejected.countDown();
                     }

                 });

            }

            try {
                // с таймаутом: без него сломанный брейкер вешает сборку вместо падения
                assertThat(probeStarted.await(5, TimeUnit.SECONDS)).isTrue();  // победитель внутри
                assertThat(allRejected.await(5, TimeUnit.SECONDS)).isTrue();   // семеро отказаны
            } finally {
                // в finally: иначе упавший ассёрт оставит пробу на await(),
                // а pool.close() будет ждать её завершения до суток
                releaseProbe.countDown();
            }
        }   // pool.close() (shutdown + awaitTermination) ДОЖИДАЕТСЯ пробы —
            // только после этого переход HALF_OPEN → CLOSED гарантированно завершён.
            // Если проверять state ВНУТРИ блока сразу после countDown(), главный поток
            // обгоняет пробу (она ещё не сделала CAS в onSuccess) → флаки "was HALF_OPEN".

        assertThat(invocations.get()).isEqualTo(1);
        assertThat(rejections.get()).isEqualTo(threads - 1);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    private static void expectFailure(CircuitBreaker circuitBreaker) {
        assertThatThrownBy(()->circuitBreaker.call(()->  {
            throw new RuntimeException("PSP unavailable");
        }));
    }

    private static void failureTwice(CircuitBreaker circuitBreaker) {
        expectFailure(circuitBreaker);
        expectFailure(circuitBreaker);
    }
}
