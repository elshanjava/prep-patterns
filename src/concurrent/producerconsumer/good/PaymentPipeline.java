package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// Bounded BlockingQueue: producer блокируется при полной очереди (backpressure).
// Consumer блокируется при пустой очереди — никакого busy-wait.
// Thread-safe без явных synchronized.
final class PaymentPipeline {
    private static final int CAPACITY = 5;

    // Ядовитая пилюля — маркер «поток данных кончился».
    // Сравнивается по ССЫЛКЕ (==), а не через equals: Payment — record, и обычный
    // платёж с такими же полями оказался бы равен пилюле по equals и остановил бы consumer.
    static final Payment POISON = new Payment("__POISON__", 0);

    private final LinkedBlockingQueue<Payment> queue = new LinkedBlockingQueue<>(CAPACITY);

    void produce(Payment p) throws InterruptedException {
        queue.put(p); // блокируется если очередь полна — backpressure!
        System.out.println("  [producer] added " + p.id() + ", queue size=" + queue.size());
    }

    Payment consume() throws InterruptedException {
        return queue.take(); // блокируется если пусто — никакого busy-wait
    }

    // Пилюлю съедает РОВНО ОДИН consumer, поэтому кладём по штуке на каждого.
    // put (а не offer): очередь может быть полна, и пилюля обязана дождаться места,
    // иначе часть consumer'ов зависнет на take() навсегда.
    void shutdown(int consumers) throws InterruptedException {
        for (int i = 0; i < consumers; i++) {
            queue.put(POISON);
        }
        System.out.println("  [producer] отправил " + consumers + " ядовитых пилюль");
    }

    int size() { return queue.size(); }

    int capacity() { return CAPACITY; }
}
