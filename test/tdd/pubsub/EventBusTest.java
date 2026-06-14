package tdd.pubsub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * Publisher-Subscriber (EventBus) — TDD kata (Revolut Live Coding).
 *
 * Именно эта задача была у реального кандидата.
 * Требования добавляются постепенно:
 *   1. subscribe + publish базовый
 *   2. несколько подписчиков — все получают
 *   3. unsubscribe — больше не получает
 *   4. исключение в одном listener не ломает остальных
 *   5. типизированные события — только нужный тип получает
 *   6. publish без подписчиков — нет исключения
 *   7. thread-safe concurrent publish
 */
class EventBusTest {

    record PaymentCompleted(String paymentId, long amount) {}
    record PaymentFailed(String paymentId, String reason) {}

    private EventBus bus;

    @BeforeEach
    void setUp() {
        bus = new EventBus();
    }

    // ── Шаг 1: один подписчик получает событие ───────────────────────────────

    @Test
    void publish_singleSubscriberReceivesEvent() {
        List<PaymentCompleted> received = new ArrayList<>();

        bus.subscribe(PaymentCompleted.class, received::add);
        bus.publish(new PaymentCompleted("pay-1", 1000L));

        assertThat(received).hasSize(1);
        assertThat(received.get(0).paymentId()).isEqualTo("pay-1");
    }

    // ── Шаг 2: несколько подписчиков — все получают ──────────────────────────

    @Test
    void publish_multipleSubscribersAllReceive() {
        List<String> log = new ArrayList<>();

        bus.subscribe(PaymentCompleted.class, e -> log.add("listener-1: " + e.paymentId()));
        bus.subscribe(PaymentCompleted.class, e -> log.add("listener-2: " + e.paymentId()));
        bus.subscribe(PaymentCompleted.class, e -> log.add("listener-3: " + e.paymentId()));

        bus.publish(new PaymentCompleted("pay-1", 500L));

        assertThat(log).hasSize(3)
                .contains("listener-1: pay-1", "listener-2: pay-1", "listener-3: pay-1");
    }

    // ── Шаг 3: unsubscribe — больше не получает ──────────────────────────────

    @Test
    void unsubscribe_listenerStopsReceivingEvents() {
        List<PaymentCompleted> received = new ArrayList<>();
        var listener = (java.util.function.Consumer<PaymentCompleted>) received::add;

        bus.subscribe(PaymentCompleted.class, listener);
        bus.publish(new PaymentCompleted("pay-1", 100L));

        bus.unsubscribe(PaymentCompleted.class, listener);
        bus.publish(new PaymentCompleted("pay-2", 200L));

        assertThat(received).hasSize(1);
        assertThat(received.get(0).paymentId()).isEqualTo("pay-1");
    }

    // ── Шаг 4: исключение в listener не ломает остальных ────────────────────

    @Test
    void publish_exceptionInOneListenerDoesNotStopOthers() {
        List<String> log = new ArrayList<>();

        bus.subscribe(PaymentCompleted.class, e -> { throw new RuntimeException("boom"); });
        bus.subscribe(PaymentCompleted.class, e -> log.add("listener-2 received"));

        assertThatCode(() -> bus.publish(new PaymentCompleted("pay-1", 100L)))
                .doesNotThrowAnyException();
        assertThat(log).containsExactly("listener-2 received");
    }

    // ── Шаг 5: типизированные события — только нужный тип получает ──────────

    @Test
    void publish_onlyMatchingTypeSubscribersReceive() {
        List<Object> completedLog = new ArrayList<>();
        List<Object> failedLog   = new ArrayList<>();

        bus.subscribe(PaymentCompleted.class, completedLog::add);
        bus.subscribe(PaymentFailed.class,    failedLog::add);

        bus.publish(new PaymentCompleted("pay-1", 100L));
        bus.publish(new PaymentFailed("pay-2", "insufficient funds"));

        assertThat(completedLog).hasSize(1);
        assertThat(failedLog).hasSize(1);
        assertThat(completedLog.get(0)).isInstanceOf(PaymentCompleted.class);
        assertThat(failedLog.get(0)).isInstanceOf(PaymentFailed.class);
    }

    // ── Шаг 6: publish без подписчиков — нет исключения ─────────────────────

    @Test
    void publish_noSubscribers_doesNotThrow() {
        assertThatCode(() -> bus.publish(new PaymentCompleted("pay-1", 100L)))
                .doesNotThrowAnyException();
    }

    // ── Шаг 7: thread-safe — concurrent publish ──────────────────────────────

    @Test
    void publish_threadSafe_allEventsDelivered() throws InterruptedException {
        int threads = 20;
        List<PaymentCompleted> received = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        bus.subscribe(PaymentCompleted.class, received::add);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                final int id = i;
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        bus.publish(new PaymentCompleted("pay-" + id, id * 100L));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        assertThat(received).hasSize(threads);
    }
}
