package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// Bounded BlockingQueue: producer блокируется при полной очереди (backpressure).
// Consumer блокируется при пустой очереди — никакого busy-wait.
// Thread-safe без явных synchronized.
final class PaymentPipeline {
    private static final int CAPACITY = 5;
    private final LinkedBlockingQueue<Payment> queue = new LinkedBlockingQueue<>(CAPACITY);

    void produce(Payment p) throws InterruptedException {
        queue.put(p); // блокируется если очередь полна — backpressure!
        System.out.println("  [producer] added " + p.id() + ", queue size=" + queue.size());
    }

    Payment consume() throws InterruptedException {
        return queue.take(); // блокируется если пусто — никакого busy-wait
    }

    int size() { return queue.size(); }

    int capacity() { return CAPACITY; }
}
