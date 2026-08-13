package concurrent.zpractice.producerconsumer;

import java.util.concurrent.LinkedBlockingQueue;

public class PaymentPipelineTestImpl {
    private static final int CAPACITY = 5;
    private final LinkedBlockingQueue<Payment> queue = new LinkedBlockingQueue<>(5);

    void produce(Payment p) {
        queue.add(p);
        System.out.println("  [producer] added " + p.id() + ", queue size=" + queue.size());
    }

    Payment consume() throws InterruptedException {
        return queue.take();
    }
}
