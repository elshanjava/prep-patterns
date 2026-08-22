package concurrent.zpractice.producerconsumer;

import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerDemo {
    private static final int CONSUMERS = 2;

    public static void main(String[] args) throws InterruptedException {
        var paymentPipeline = new PaymentPipelineTestImpl();

        Thread producer = new Thread(()->{
            try {
                for (int i = 0; i < 10; i++) {
                    paymentPipeline.produce(new Payment("payment-" + i, i * 100L));
                }
                paymentPipeline.shutdown(CONSUMERS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < CONSUMERS; i++) {
            Thread consumer = new Thread(()->{
                String threadName = Thread.currentThread().getName();
                try {
                    while (true) {
                        Payment consume = paymentPipeline.consume();
                        if (consume == PaymentPipelineTestImpl.POISON) {      // именно ==, не equals
                            System.out.println("  [" + threadName + "] пилюля -> выхожу");
                            break;
                        }
                        System.out.println("Consume payment: " + consume.toString() + " via thread " + threadName);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "consume-" + i);
            consumers.add(consumer);
        }

        consumers.forEach(Thread::start);
        producer.start();
        producer.join();
        for (Thread consumer : consumers) {
            consumer.join();
        }

    }
}
