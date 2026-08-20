package concurrent.zpractice.producerconsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class PaymentPipelineTestImpl {
    private static final int CAPACITY = 5;
    private static final Payment POISON =
            new Payment("__POISON__", 0);

    private final LinkedBlockingQueue<Payment> queue = new LinkedBlockingQueue<>(CAPACITY);

    void produce(Payment p) throws InterruptedException {
        queue.put(p);
        System.out.println("  [producer] added " + p.id() + ", queue size=" + queue.size());
    }

    Payment consume() throws InterruptedException {
        return queue.take();
    }

    void shutdown(int consumersQuantity) throws InterruptedException {
        for (int i = 0; i < consumersQuantity; i++) {
            queue.put(POISON);
        }
        System.out.println("  [producer] отправил " + consumersQuantity + " ядовитых пилюль");
    }
}
