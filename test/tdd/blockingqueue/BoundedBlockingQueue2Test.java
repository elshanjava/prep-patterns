package tdd.blockingqueue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.blockingqueue.BoundedBlockingQueue2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BoundedBlockingQueue2Test {

    private BoundedBlockingQueue2<String> blockingQueue2;

    @BeforeEach
    void setUp() {
        blockingQueue2 = new BoundedBlockingQueue2<>(3);
    }

    @Test
    void putThenTakeElement() throws InterruptedException {
        blockingQueue2.put("a");
        assertThat(blockingQueue2.take()).isEqualTo("a");
    }

    @Test
    void putAndReturnInFifoOrder() throws InterruptedException {
        blockingQueue2.put("a");
        blockingQueue2.put("b");

        assertThat(blockingQueue2.take()).isEqualTo("a");
        assertThat(blockingQueue2.take()).isEqualTo("b");
    }

    @Test
    void shouldPutInTheStart() throws InterruptedException {
        blockingQueue2.put("a");
        blockingQueue2.put("b");
        blockingQueue2.put("c");

        blockingQueue2.take();
        blockingQueue2.put("e");
        assertThat(blockingQueue2.take()).isEqualTo("b");
        assertThat(blockingQueue2.take()).isEqualTo("c");
        assertThat(blockingQueue2.take()).isEqualTo("e");

    }

    @Test
    void shouldBlockThenNoHaveCapacity() throws InterruptedException {
        blockingQueue2.put("a");
        blockingQueue2.put("b");
        blockingQueue2.put("c");

        var passed = new CountDownLatch(1);

        Thread producer = new Thread(() -> {
            try {
                blockingQueue2.put("e");
                passed.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        producer.setDaemon(true);
        producer.start();

        try {
            assertThat(passed.await(100, TimeUnit.MILLISECONDS)).isFalse();
            assertThat(producer.getState()).isEqualTo(Thread.State.WAITING);
        } finally {
            blockingQueue2.take();
        }

        assertThat(producer.join(Duration.ofSeconds(5))).isTrue();

        assertThat(passed.await(100, TimeUnit.MILLISECONDS)).isTrue();

        assertThat(blockingQueue2.take()).isEqualTo("b");
        assertThat(blockingQueue2.take()).isEqualTo("c");
        assertThat(blockingQueue2.take()).isEqualTo("e");
    }

    @Test
    void shouldThrowExceptionThenInputNull() throws InterruptedException {
        assertThatThrownBy(() -> blockingQueue2.put(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionThenCapacityLessOne() {
        assertThatThrownBy(() -> new BoundedBlockingQueue2<>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldBlockingTakeThenEmpty() throws InterruptedException {
        var passed = new CountDownLatch(1);
        AtomicReference<String> take = new AtomicReference<>();
        Thread consumer = new Thread(() -> {
            try {
                take.set(blockingQueue2.take());
                passed.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        try {
            assertThat(passed.await(100, TimeUnit.MILLISECONDS)).isFalse();
            assertThat(consumer.getState()).isEqualTo(Thread.State.WAITING);
        } finally {
            blockingQueue2.put("a");
        }



        assertThat(consumer.join(Duration.ofMillis(100))).isTrue();
        assertThat(passed.await(100, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(take.get()).isEqualTo("a");

    }

    @Test
    void manyProducersAndConsumers_deliverEveryElementExactlyOnce() throws InterruptedException {
        int producer = 4;
        int consumer = 4;
        int perProducer = 250;
        int total = producer * perProducer;

        var blockingQueue = new BoundedBlockingQueue2<Integer>(producer + consumer);
        var received = new LinkedBlockingDeque<Integer>();
        var done = new CountDownLatch(producer + consumer);

        try (ExecutorService pool = Executors.newFixedThreadPool(producer + consumer)) {
            for (int i = 0; i < producer; i++) {
                int finalI = i * perProducer;
                pool.submit(()-> {
                    try {
                        for (int j = 0; j < perProducer; j++) {
                            blockingQueue.put(finalI + j);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                 });
            }
            for (int i = 0; i < consumer; i++) {
                pool.submit(()-> {
                    try {
                        for (int j = 0; j < total/consumer; j++) {
                            received.add(blockingQueue.take());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });

            }
            assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        }

        List<Integer> all = new ArrayList<>(received);
        Assertions.assertThat(all).hasSize(total);
        Assertions.assertThat(all).doesNotHaveDuplicates();
        Assertions.assertThat(blockingQueue.size()).isZero();


    }
}
