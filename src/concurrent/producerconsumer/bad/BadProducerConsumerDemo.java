package concurrent.producerconsumer.bad;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.atomic.AtomicInteger;

public class BadProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ProducerConsumer [BAD] — busy-wait + unbounded ArrayList ==");

        var pipeline = new BadPaymentPipeline();
        int maxQueueSize = 0;
        AtomicInteger maxSeen = new AtomicInteger();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                pipeline.produce(new Payment("pay-" + i, i * 100L));
                int size = pipeline.size();
                maxSeen.updateAndGet(cur -> Math.max(cur, size));
                try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Payment p = pipeline.consume(); // busy-wait внутри
                    System.out.println("  [consumer] processed " + p.id());
                    Thread.sleep(50); // consumer медленнее producer
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - очередь росла до " + maxSeen.get() + " элементов (unbounded — при высокой нагрузке OOM)");
        System.out.println("  - busy-wait в consume(): CPU крутится даже когда нечего делать");
        System.out.println("  - нет backpressure: producer не замедляется при перегрузке consumer");
        System.out.println("  - synchronized ArrayList не масштабируется на несколько consumers");
    }
}
