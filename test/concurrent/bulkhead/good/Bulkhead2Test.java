package concurrent.bulkhead.good;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Bulkhead2Test {

    @Test
    void allowsCallWhenPermitsAvailable() throws InterruptedException {
        var bulkhead = new Bulkhead(2, 50);

        assertThat(bulkhead.call(()-> "ok")).isEqualTo("ok");
        assertThat(bulkhead.availablePermits()).isEqualTo(2);
    }

    @Test
    void rejectsWhenAllPermitsAreTaken() throws InterruptedException {
        var bulkhead = new Bulkhead(2, 50);

        var holding = new CountDownLatch(2);
        var release = new CountDownLatch(1);

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            for (int i = 0; i < 2; i++) {
                pool.submit(()-> bulkhead.call(()-> {
                        // countDown ВНУТРИ действия: значит разрешение уже захвачено.
                        // Снаружи (в main, до submit) он обнулял бы счётчик до того,
                        // как воркеры вообще стартовали — await проскакивал бы впустую.
                        holding.countDown();
                        try {
                            release.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return "";
                }));
            }

            try {
                // с таймаутом: сломанная переборка должна давать красный тест,
                // а не вечное ожидание
                assertThat(holding.await(5, TimeUnit.SECONDS)).isTrue();

                assertThatThrownBy(()->bulkhead.call(()-> "third"))
                        .isInstanceOf(BulkheadFullException.class);
            } finally {
                // в finally: упади ассёрт выше — воркеры остались бы на release.await(),
                // а pool.close() ждёт завершения до суток. Тест повис бы вместо падения.
                release.countDown();
            }
        }

        assertThat(bulkhead.rejectedCount()).isEqualTo(1);
    }

    @Test
    void rejectionTakesAboutWaitTimeout_notTheCallDuration() throws InterruptedException {
        var bulkhead = new Bulkhead(1, 100);

        // 1, а не 2: воркер один, значит и сигнал будет ровно один.
        var holding = new CountDownLatch(1);
        var release = new CountDownLatch(1);

        try (ExecutorService pool = Executors.newFixedThreadPool(1)) {
            pool.submit(()-> bulkhead.call(()-> {
                holding.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "";
            }));

            // Всё измерение — ВНУТРИ try. Снаружи pool.close() успел бы начать ждать
            // воркера раньше, чем кто-либо отпустит release: гарантированный дедлок.
            try {
                assertThat(holding.await(5, TimeUnit.SECONDS)).isTrue();

                long start = System.currentTimeMillis();
                assertThatThrownBy(() -> bulkhead.call(() -> "rejected"))
                        .isInstanceOf(BulkheadFullException.class);
                long elapsed = System.currentTimeMillis() - start;

                assertThat(elapsed).isGreaterThanOrEqualTo(100);
                assertThat(elapsed).isLessThan(2_000);
            } finally {
                release.countDown();
            }
        }
    }

    @Test
    void releasesPermitAfterNormalCompletion() throws InterruptedException {
        var bulkhead = new Bulkhead(1, 50);

        bulkhead.call(() -> "first");

        assertThat(bulkhead.availablePermits()).isEqualTo(1);
        assertThat(bulkhead.call(() -> "second")).isEqualTo("second");
    }

    @Test
    void releasesPermitWhenActionThrows() throws InterruptedException {
        var bulkhead = new Bulkhead(1, 50);

        assertThatThrownBy(() -> bulkhead.call(() -> {
            throw new RuntimeException("PSP unavailable");
        })).isInstanceOf(RuntimeException.class);

        // Без release() в finally здесь было бы 0, и следующий вызов упал бы отказом
        // при полностью живой зависимости.
        assertThat(bulkhead.availablePermits()).isEqualTo(1);
        assertThat(bulkhead.call(() -> "still works")).isEqualTo("still works");
    }

    @Test
    void survivesManyFailures_withoutLeakingPermits() throws InterruptedException {
        var bulkhead = new Bulkhead(3, 50);

        for (int i = 0; i < 50; i++) {
            assertThatThrownBy(() -> bulkhead.call(() -> {
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class);
        }

        assertThat(bulkhead.availablePermits()).isEqualTo(3);
    }

    @Test
    void concurrencyNeverExceedsLimit() throws InterruptedException {
        int limit   = 4;
        int threads = 24;
        var bulkhead = new Bulkhead(limit, 5_000);   // ждём долго — отказов быть не должно
        var completed = new AtomicInteger();
        var ready = new CountDownLatch(threads);
        var done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(()-> {
                    ready.countDown();
                    try {
                        ready.await();
                        bulkhead.call(()-> {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return "ok";
                        });
                        completed.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();;
                    } finally {
                        done.countDown();
                    }

                });
            }
            done.await();
        }

        assertThat(bulkhead.maxObservedConcurrency()).isLessThanOrEqualTo(limit);
        assertThat(bulkhead.rejectedCount()).isZero();
        assertThat(completed.get()).isEqualTo(threads);
        assertThat(bulkhead.availablePermits()).isEqualTo(limit);

    }
}
