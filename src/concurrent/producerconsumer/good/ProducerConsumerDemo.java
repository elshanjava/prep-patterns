package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerDemo {
    private static final int CONSUMERS = 2;   // pipeline менять не пришлось — только это число

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ProducerConsumer [GOOD] — LinkedBlockingQueue(capacity=5), "
                         + CONSUMERS + " consumer'а, остановка ядовитой пилюлей ==");

        var pipeline  = new PaymentPipeline();
        var maxSeen   = new AtomicInteger();
        var processed = new AtomicInteger();

        // try ОБЁРНУТ ВОКРУГ цикла: InterruptedException выносит нас из него.
        // Если ловить внутри цикла и продолжать, поток игнорирует прерывание —
        // остановить его станет нечем.
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    pipeline.produce(new Payment("pay-" + i, i * 100L));
                    int size = pipeline.size();
                    maxSeen.updateAndGet(cur -> Math.max(cur, size));
                    Thread.sleep(10);
                }
                pipeline.shutdown(CONSUMERS);   // по пилюле на каждого consumer'а
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();   // восстановили флаг и вышли
            }
        }, "producer");

        List<Thread> consumers = new ArrayList<>();
        for (int c = 1; c <= CONSUMERS; c++) {
            Thread t = new Thread(() -> {
                String me = Thread.currentThread().getName();
                try {
                    while (true) {
                        Payment p = pipeline.consume();
                        if (p == PaymentPipeline.POISON) {      // именно ==, не equals
                            System.out.println("  [" + me + "] пилюля -> выхожу");
                            break;
                        }
                        System.out.println("  [" + me + "] processed " + p.id());
                        processed.incrementAndGet();
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("  [" + me + "] прерван -> выхожу");
                }
            }, "consumer-" + c);
            consumers.add(t);
        }

        consumers.forEach(Thread::start);
        producer.start();
        producer.join();
        for (Thread t : consumers) t.join();     // все вышли сами, без interrupt

        System.out.println();
        System.out.printf("обработано платежей: %d из 10%n", processed.get());
        System.out.printf("макс. размер очереди: %d (capacity=%d — backpressure сработал)%n",
                maxSeen.get(), pipeline.capacity());
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - backpressure: producer заблокировался когда queue.size() == 5");
        System.out.println("  - нет busy-wait: consumer спит в take() без CPU-нагрузки");
        System.out.println("  - масштабируется: " + CONSUMERS + " consumer'а — 0 изменений в pipeline");
        System.out.println("  - остановка без флагов и interrupt: пилюля в очереди, по одной на поток");
        System.out.println("  - в Spring: @RabbitListener + RabbitTemplate — тот же паттерн поверх MQ");
    }
}
