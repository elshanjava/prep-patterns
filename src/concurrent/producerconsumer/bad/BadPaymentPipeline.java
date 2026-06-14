package concurrent.producerconsumer.bad;

import concurrent.producerconsumer.model.Payment;

import java.util.ArrayList;
import java.util.List;

// Разделяемый ArrayList без ограничения размера.
// Consumer busy-wait: жрёт CPU даже когда очередь пуста.
// Быстрый producer + медленный consumer → очередь растёт без предела → OOM.
// synchronized на каждой операции — producer и consumer всё равно сериализованы.
final class BadPaymentPipeline {
    private final List<Payment> queue = new ArrayList<>();

    synchronized void produce(Payment p) {
        queue.add(p);
        System.out.println("  [producer] added " + p.id() + ", queue size=" + queue.size());
    }

    // Busy-wait: постоянно проверяет очередь даже когда она пуста
    Payment consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!queue.isEmpty()) {
                    return queue.remove(0);
                }
            }
            Thread.sleep(1); // минимальная пауза — всё равно CPU waste
        }
    }

    synchronized int size() { return queue.size(); }
}
