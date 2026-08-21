package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Тесты на очередь конвейера.
 *
 * Демка печатала "backpressure сработал", опираясь на то, что размер очереди достиг
 * потолка. Но достичь потолка и ЗАБЛОКИРОВАТЬ продюсера — разные утверждения:
 * первое верно и для очереди, которая молча теряет лишнее. Здесь блокировка проверяется
 * явно, а не выводится из косвенного признака.
 */
class PaymentPipelineTest {

    private PaymentPipeline pipeline;

    @BeforeEach
    void setUp() {
        pipeline = new PaymentPipeline();
    }

    // ── FIFO ─────────────────────────────────────────────────────────────────

    @Test
    void consume_returnsPaymentsInFifoOrder() throws InterruptedException {
        pipeline.produce(new Payment("pay-1", 100));
        pipeline.produce(new Payment("pay-2", 200));

        assertThat(pipeline.consume().id()).isEqualTo("pay-1");
        assertThat(pipeline.consume().id()).isEqualTo("pay-2");
    }

    // ── Backpressure: продюсер РЕАЛЬНО блокируется ───────────────────────────

    @Test
    void produce_blocksWhenQueueIsFull_untilSpaceFreed() throws InterruptedException {
        fillToCapacity();
        assertThat(pipeline.size()).isEqualTo(pipeline.capacity());

        var passed = new CountDownLatch(1);
        Thread extra = new Thread(() -> {
            try {
                pipeline.produce(new Payment("overflow", 999));
                passed.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "extra-producer");
        extra.start();

        // Не прошёл за 200ms — значит стоит на put(), а не потерял платёж.
        assertThat(passed.await(200, TimeUnit.MILLISECONDS)).isFalse();

        pipeline.consume();                                  // освободили одно место

        assertThat(passed.await(1, TimeUnit.SECONDS)).isTrue();
        extra.join();
        assertThat(pipeline.size()).isEqualTo(pipeline.capacity());
    }

    // ── Consumer блокируется на пустой очереди, а не крутит CPU ──────────────

    @Test
    void consume_blocksWhenQueueIsEmpty_untilPaymentArrives() throws InterruptedException {
        var got = new CountDownLatch(1);
        Thread consumer = new Thread(() -> {
            try {
                pipeline.consume();
                got.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "waiting-consumer");
        consumer.start();

        assertThat(got.await(200, TimeUnit.MILLISECONDS)).isFalse();

        pipeline.produce(new Payment("pay-1", 100));

        assertThat(got.await(1, TimeUnit.SECONDS)).isTrue();
        consumer.join();
    }

    // ── Ядовитая пилюля ──────────────────────────────────────────────────────

    @Test
    void shutdown_enqueuesOnePillPerConsumer() throws InterruptedException {
        pipeline.shutdown(3);

        assertThat(pipeline.size()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            assertThat(pipeline.consume()).isSameAs(PaymentPipeline.POISON);
        }
    }

    @Test
    void poisonPill_mustBeComparedByReference_notEquals() {
        // Payment — record, поэтому обычный платёж с теми же полями РАВЕН пилюле.
        // Если сравнивать через equals, такой платёж остановит consumer'а, и остаток
        // очереди будет потерян. Этот тест фиксирует, почему в коде стоит ==.
        Payment lookalike = new Payment("__POISON__", 0);

        assertThat(lookalike).isEqualTo(PaymentPipeline.POISON);
        assertThat(lookalike).isNotSameAs(PaymentPipeline.POISON);
    }

    @Test
    void shutdown_waitsForSpace_ratherThanDroppingPills() throws InterruptedException {
        fillToCapacity();                                    // места нет вообще

        var sent = new CountDownLatch(1);
        Thread stopper = new Thread(() -> {
            try {
                pipeline.shutdown(1);
                sent.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "stopper");
        stopper.start();

        // offer() вернул бы false и пилюля пропала бы — consumer завис бы на take() навсегда.
        assertThat(sent.await(200, TimeUnit.MILLISECONDS)).isFalse();

        pipeline.consume();

        assertThat(sent.await(1, TimeUnit.SECONDS)).isTrue();
        stopper.join();
    }

    private void fillToCapacity() throws InterruptedException {
        for (int i = 0; i < pipeline.capacity(); i++) {
            pipeline.produce(new Payment("pay-" + i, i * 100L));
        }
    }
}
