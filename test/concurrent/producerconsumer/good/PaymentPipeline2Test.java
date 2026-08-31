package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

public class PaymentPipeline2Test {

    private PaymentPipeline pipeline;

    @BeforeEach
    void setUo() {
        pipeline = new PaymentPipeline();
    }

    @Test
    void consume_returnPaymentInFifoOrder() throws InterruptedException {
        pipeline.produce(new Payment("pay-1", 100));
        pipeline.produce(new Payment("pay-2", 200));

        assertThat(pipeline.consume().id()).isEqualTo("pay-1");
        assertThat(pipeline.consume().id()).isEqualTo("pay-2");
    }

    @Test
    void produce_blocksWhenQueueIsFull_untilSpaceFreed() throws InterruptedException {
        fillToCapacity();
        assertThat(pipeline.size()).isEqualTo(pipeline.capacity());

        var passed = new CountDownLatch(1);
        Thread extra = new Thread(()-> {
            try {
                pipeline.produce(new Payment("overflow", 999));
                passed.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "extra-producer");
        extra.start();

        assertThat(passed.await(300, TimeUnit.MILLISECONDS)).isFalse();

        pipeline.consume();

        assertThat(passed.await(1, TimeUnit.MILLISECONDS)).isTrue();
        extra.join();
        assertThat(pipeline.size()).isEqualTo(pipeline.capacity());

    }

    @Test
    void consume_blocksWhenQueueIsEmpty_untilPaymentArrives() throws InterruptedException {
        var got = new CountDownLatch(1);

        Thread consumer = new Thread(()-> {
            try {
                pipeline.consume();
                got.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "waiting-consumer");
        consumer.start();

        assertThat(got.await(200, TimeUnit.MILLISECONDS)).isFalse();
        assertThat(consumer.getState()).isEqualTo(Thread.State.WAITING);

        pipeline.produce(new Payment("pay-1", 100));
        assertThat(got.await(1, TimeUnit.MILLISECONDS)).isTrue();
        consumer.join();
    }

    @Test
    void shutdown_enqueuesOnePillPerConsumer() throws InterruptedException {
        pipeline.shutdown(3);

        assertThat(pipeline.size()).isEqualTo(3);
        for (int i = 0; i < pipeline.size(); i++) {
             assertThat(pipeline.consume()).isSameAs(PaymentPipeline.POISON);
        }
    }

    @Test
    void poisonPill_mustBeComparedByReference_notEquals() {
        Payment lookalike = new Payment("__POISON__", 0);

        assertThat(lookalike).isEqualTo(PaymentPipeline.POISON);
        assertThat(lookalike).isNotSameAs(PaymentPipeline.POISON);
    }

    @Test
    void shutdown_waitsForSpace_ratherThanDroppingPills() throws InterruptedException {
        fillToCapacity();

        var sent = new CountDownLatch(1);
         Thread stopper = new Thread(()-> {
             try {
                 pipeline.shutdown(1);
                 sent.countDown();
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
         });
         stopper.start();

         assertThat(sent.await(200, TimeUnit.MILLISECONDS)).isFalse();
         assertThat(stopper.getState()).isEqualTo(Thread.State.WAITING);

         pipeline.consume();

         assertThat(sent.await(1, TimeUnit.MILLISECONDS)).isTrue();
         stopper.join();

    }

    private void fillToCapacity() throws InterruptedException {
        for (int i = 0; i < pipeline.capacity(); i++) {
             pipeline.produce(new Payment("pay-" + i, i * 100L));
        }
    }
}
