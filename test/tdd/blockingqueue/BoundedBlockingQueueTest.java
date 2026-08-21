package tdd.blockingqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Bounded BlockingQueue с нуля — TDD kata.
 *
 * "Реализуй BlockingQueue, не используя java.util.concurrent.BlockingQueue."
 * Проверяют не алгоритм (кольцевой буфер тривиален), а понимание условных переменных:
 * while против if, два условия против одного монитора, signal против signalAll.
 *
 *   1. положили — забрали
 *   2. FIFO
 *   3. take блокируется на пустой, пока не появится элемент
 *   4. put блокируется на полной, пока не освободится место
 *   5. кольцо: работает за пределами capacity операций
 *   6. poll/offer с таймаутом сдаются, а не висят
 *   7. null отвергается
 *   8. неположительная ёмкость отвергается
 *   9. под нагрузкой ничего не теряется и не дублируется
 */
class BoundedBlockingQueueTest {

    private BoundedBlockingQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new BoundedBlockingQueue<>(3);
    }

    // ── Базовое ──────────────────────────────────────────────────────────────

    @Test
    void put_thenTake_returnsElement() throws InterruptedException {
        queue.put("a");

        assertThat(queue.take()).isEqualTo("a");
        assertThat(queue.size()).isZero();
    }

    @Test
    void take_returnsElementsInFifoOrder() throws InterruptedException {
        queue.put("a");
        queue.put("b");
        queue.put("c");

        assertThat(queue.take()).isEqualTo("a");
        assertThat(queue.take()).isEqualTo("b");
        assertThat(queue.take()).isEqualTo("c");
    }

    // ── Блокировка ───────────────────────────────────────────────────────────

    @Test
    void take_blocksOnEmpty_untilElementArrives() throws InterruptedException {
        var got = new CountDownLatch(1);
        var received = new ArrayList<String>();

        Thread consumer = new Thread(() -> {
            try {
                received.add(queue.take());
                got.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "consumer");
        consumer.start();

        assertThat(got.await(200, TimeUnit.MILLISECONDS)).isFalse();   // висит на take()

        queue.put("late");

        assertThat(got.await(1, TimeUnit.SECONDS)).isTrue();
        consumer.join();
        assertThat(received).containsExactly("late");
    }

    @Test
    void put_blocksOnFull_untilSpaceFreed() throws InterruptedException {
        queue.put("a");
        queue.put("b");
        queue.put("c");                                    // ёмкость 3 — полна
        assertThat(queue.size()).isEqualTo(3);

        var passed = new CountDownLatch(1);
        Thread producer = new Thread(() -> {
            try {
                queue.put("d");
                passed.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "producer");
        producer.start();

        assertThat(passed.await(200, TimeUnit.MILLISECONDS)).isFalse();

        assertThat(queue.take()).isEqualTo("a");           // освободили место

        assertThat(passed.await(1, TimeUnit.SECONDS)).isTrue();
        producer.join();
        assertThat(queue.size()).isEqualTo(3);
    }

    // ── Кольцевой буфер ──────────────────────────────────────────────────────

    @Test
    void worksBeyondCapacity_wrappingAround() throws InterruptedException {
        // 10 операций на буфере из 3 — head и tail обязаны несколько раз обойти круг
        for (int i = 0; i < 10; i++) {
            queue.put("item-" + i);
            assertThat(queue.take()).isEqualTo("item-" + i);
        }
        assertThat(queue.size()).isZero();
    }

    // ── Таймауты ─────────────────────────────────────────────────────────────

    @Test
    void poll_returnsNullWhenNothingArrivesInTime() throws InterruptedException {
        long start = System.currentTimeMillis();

        assertThat(queue.poll(100, TimeUnit.MILLISECONDS)).isNull();

        assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(100);
    }

    @Test
    void offer_returnsFalseWhenNoSpaceFreesInTime() throws InterruptedException {
        queue.put("a");
        queue.put("b");
        queue.put("c");

        assertThat(queue.offer("d", 100, TimeUnit.MILLISECONDS)).isFalse();
        assertThat(queue.size()).isEqualTo(3);
    }

    @Test
    void offer_succeedsWhenSpaceIsAvailable() throws InterruptedException {
        assertThat(queue.offer("a", 100, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(queue.take()).isEqualTo("a");
    }

    // ── Краевые случаи ───────────────────────────────────────────────────────

    @Test
    void put_rejectsNull() {
        assertThatThrownBy(() -> queue.put(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_rejectsNonPositiveCapacity() {
        assertThatThrownBy(() -> new BoundedBlockingQueue<String>(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");
    }

    // ── Под нагрузкой ────────────────────────────────────────────────────────

    @Test
    void manyProducersAndConsumers_deliverEveryElementExactlyOnce() throws InterruptedException {
        int producers   = 4;
        int consumers   = 4;
        int perProducer = 250;
        int total       = producers * perProducer;

        var q        = new BoundedBlockingQueue<Integer>(8);   // намеренно тесная
        var received = new ConcurrentLinkedQueue<Integer>();
        var done     = new CountDownLatch(producers + consumers);

        try (ExecutorService pool = Executors.newFixedThreadPool(producers + consumers)) {
            for (int p = 0; p < producers; p++) {
                int base = p * perProducer;
                pool.submit(() -> {
                    try {
                        for (int i = 0; i < perProducer; i++) q.put(base + i);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            for (int c = 0; c < consumers; c++) {
                pool.submit(() -> {
                    try {
                        for (int i = 0; i < total / consumers; i++) received.add(q.take());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        }

        // Ни одного потерянного и ни одного задвоенного: 1000 различных чисел.
        List<Integer> all = new ArrayList<>(received);
        assertThat(all).hasSize(total);
        assertThat(all).doesNotHaveDuplicates();
        assertThat(q.size()).isZero();
    }
}
