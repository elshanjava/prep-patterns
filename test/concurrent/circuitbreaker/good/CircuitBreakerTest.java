package concurrent.circuitbreaker.good;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Тесты на машину состояний CircuitBreaker.
 *
 * Мотивация: демка однопоточная, поэтому две ветки в call() не исполнялись НИКОГДА —
 * "probe already in flight" (проигрыш CAS) и "probe in flight" (вход при HALF_OPEN).
 * Проверить их можно только несколькими потоками, чем и занят последний тест.
 *
 * Тест в том же пакете, что и класс: CircuitBreaker и CircuitOpenException
 * package-private, публиковать их ради тестов не нужно.
 */
class CircuitBreakerTest {

    // ── Базовые переходы ─────────────────────────────────────────────────────

    @Test
    void startsClosed() {
        var cb = new CircuitBreaker(3, 100);

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void staysClosedWhileFailuresBelowThreshold() {
        var cb = new CircuitBreaker(3, 100);

        failTwice(cb);

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void opensWhenThresholdReached() {
        var cb = new CircuitBreaker(3, 100);

        failTwice(cb);
        expectFailure(cb);                       // третий — порог

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void successResetsFailureCounter() {
        var cb = new CircuitBreaker(3, 100);

        failTwice(cb);
        cb.call(() -> "ok");                     // счётчик обнулился
        failTwice(cb);                           // снова только два подряд

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    // ── OPEN: быстрый отказ БЕЗ сетевого вызова ──────────────────────────────

    @Test
    void whenOpen_failsFastWithoutInvokingAction() {
        var cb = new CircuitBreaker(1, 10_000);  // окно заведомо не истечёт
        expectFailure(cb);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);

        var invocations = new AtomicInteger();

        assertThatThrownBy(() -> cb.call(() -> {
            invocations.incrementAndGet();
            return "should not happen";
        })).isInstanceOf(CircuitOpenException.class);

        // Главная ценность паттерна: до сети дело не дошло вообще.
        assertThat(invocations.get()).isZero();
    }

    // ── HALF_OPEN: проба и оба её исхода ─────────────────────────────────────

    @Test
    void afterRecoveryTimeout_probeSucceeds_closesCircuit() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);

        Thread.sleep(80);                        // окно восстановления истекло

        assertThat(cb.call(() -> "recovered")).isEqualTo("recovered");
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void afterRecoveryTimeout_probeFails_reopensCircuit() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);

        Thread.sleep(80);

        expectFailure(cb);                       // проба упала

        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void failedProbe_startsNewRecoveryWindow() throws InterruptedException {
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        Thread.sleep(80);
        expectFailure(cb);                       // проба упала, окно отсчитывается заново

        var invocations = new AtomicInteger();
        assertThatThrownBy(() -> cb.call(() -> {
            invocations.incrementAndGet();
            return "x";
        })).isInstanceOf(CircuitOpenException.class);

        assertThat(invocations.get()).isZero();  // сразу после провала пробы — снова fast-fail
    }

    // ── Многопоточность: ровно ОДНА проба ────────────────────────────────────

    @Test
    void inHalfOpen_onlyOneProbeIsAllowed_othersFailFast() throws InterruptedException {
        int threads = 8;
        var cb = new CircuitBreaker(1, 50);
        expectFailure(cb);
        Thread.sleep(80);                        // цепь готова принять пробу

        var invocations = new AtomicInteger();
        var rejections  = new AtomicInteger();
        var probeStarted = new CountDownLatch(1);
        var releaseProbe = new CountDownLatch(1);
        // Ждём, пока ВСЕ проигравшие получат отказ. Детерминированно: они не блокируются.
        var allRejected  = new CountDownLatch(threads - 1);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    try {
                        cb.call(() -> {
                            invocations.incrementAndGet();
                            probeStarted.countDown();
                            try {
                                releaseProbe.await();   // держим HALF_OPEN, пока идёт проба
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

            probeStarted.await();                // победитель внутри действия
            allRejected.await();                 // остальные семеро уже отказаны
            releaseProbe.countDown();            // отпускаем пробу
        }

        // Ровно один поток дошёл до "сети" — ровно ради этого паттерн и существует:
        // восстанавливающийся PSP не должен получить восемь запросов вместо одного.
        assertThat(invocations.get()).isEqualTo(1);
        assertThat(rejections.get()).isEqualTo(threads - 1);
        assertThat(cb.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static void failTwice(CircuitBreaker cb) {
        expectFailure(cb);
        expectFailure(cb);
    }

    private static void expectFailure(CircuitBreaker cb) {
        assertThatThrownBy(() -> cb.call(() -> {
            throw new RuntimeException("PSP unavailable");
        })).isInstanceOf(RuntimeException.class);
    }
}
