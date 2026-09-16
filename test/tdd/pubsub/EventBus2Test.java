package tdd.pubsub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.pubsub.EventBus2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class EventBus2Test {
    record PaymentCompleted(String paymentId, BigDecimal amount){}

    private EventBus2 eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus2();
    }

    @Test
    void publish_receiveEvent() {
        List<PaymentCompleted> log = new ArrayList<>();

        var event = new PaymentCompleted("pay-1", new BigDecimal(100));
        eventBus.subscribe(PaymentCompleted.class, log::add);

        assertThat(log).isEmpty();
        eventBus.publish(event);

        assertThat(log).hasSize(1);
        assertThat(log).containsExactly(event);
    }

    @Test
    void unsubscribe_whenHaveActiveSubscription() {
        List<PaymentCompleted> log = new ArrayList<>();
        Consumer<PaymentCompleted> listener = log::add;

        eventBus.subscribe(PaymentCompleted.class, listener);
        eventBus.publish(new PaymentCompleted("pay-1", new BigDecimal(200)));

        eventBus.unsubscribe(PaymentCompleted.class, listener);
        eventBus.publish(new PaymentCompleted("pay-2", new BigDecimal(300)));

        assertThat(log).hasSize(1);
    }

    @Test
    void publish_exceptionInOneListenerDoesNotStopOthers() {
        List<String> log = new ArrayList<>();
        eventBus.subscribe(PaymentCompleted.class, e -> { throw new RuntimeException("boom"); });
        eventBus.subscribe(PaymentCompleted.class, e -> log.add("listener-2 received"));

        assertThatCode(()-> eventBus.publish(new PaymentCompleted("pay-1", new BigDecimal(100))))
                .doesNotThrowAnyException();
        assertThat(log).containsExactly("listener-2 received");
    }

    @Test
    void publish_onlyMatchingTypeReceives() {
        record PaymentFailed(String id, String reason) {}
        List<Object> completed = new ArrayList<>();
        List<Object> failed = new ArrayList<>();

        eventBus.subscribe(PaymentCompleted.class, completed::add);
        eventBus.subscribe(PaymentFailed.class, failed::add);

        eventBus.publish(new PaymentCompleted("pay-1", new BigDecimal(100)));

        assertThat(completed).hasSize(1);
        assertThat(failed).isEmpty();
    }

    @Test
    void publish_noSubscribers_doesNotThrow() {
        assertThatCode(() -> eventBus.publish(new PaymentCompleted("x", new BigDecimal(1))))
                .doesNotThrowAnyException();
    }

    @Test
    void publish_threadSafe_allEventsDelivered() throws InterruptedException {
        int thread = 50;
        var ready = new CountDownLatch(thread);
        var done = new CountDownLatch(thread);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        eventBus.subscribe(PaymentCompleted.class, e -> {});   // постоянный подписчик — чтобы список был непуст

        try (ExecutorService pool = Executors.newFixedThreadPool(thread)) {
            for (int i = 0; i < thread; i++) {
                 final int id = i;
                 pool.submit(()-> {
                     ready.countDown();
                     try {
                         ready.await();
                         for (int k = 0; k < 200; k++) {
                             if (id % 2 == 0) {
                                 eventBus.publish(new PaymentCompleted("p", new BigDecimal(1)));   // итерирует
                             } else {
                                 Consumer<PaymentCompleted> l = e -> {};
                                 eventBus.subscribe(PaymentCompleted.class, l);                     // модифицирует
                                 eventBus.unsubscribe(PaymentCompleted.class, l);
                             }
                         }
                     } catch (InterruptedException ie) {
                         Thread.currentThread().interrupt();
                     } catch (Exception e) {
                         errors.add(e);
                     } finally {
                         done.countDown();
                     }
                 });
            }
            assertThat(done.await(100, TimeUnit.MILLISECONDS)).isTrue();
        }
        assertThat(errors).isEmpty();
    }




}
