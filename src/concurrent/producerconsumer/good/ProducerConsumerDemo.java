package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ProducerConsumer [GOOD] — LinkedBlockingQueue(capacity=5) ==");

        var pipeline = new PaymentPipeline();
        AtomicInteger maxSeen = new AtomicInteger();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    pipeline.produce(new Payment("pay-" + i, i * 100L));
                    int size = pipeline.size();
                    maxSeen.updateAndGet(cur -> Math.max(cur, size));
                    Thread.sleep(10);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Payment p = pipeline.consume();
                    System.out.println("  [consumer] processed " + p.id());
                    Thread.sleep(50);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();

        System.out.println();
        System.out.printf("макс. размер очереди: %d (capacity=%d — backpressure сработал)%n",
                maxSeen.get(), pipeline.capacity());
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - backpressure: producer заблокировался когда queue.size() == 5");
        System.out.println("  - нет busy-wait: consumer спит в take() без CPU-нагрузки");
        System.out.println("  - масштабируется: добавить второй consumer — 0 изменений в pipeline");
        System.out.println("  - в Spring: @RabbitListener + RabbitTemplate — тот же паттерн поверх MQ");
    }
}
