package concurrent.zpractice.readwritelock;

import java.util.ArrayList;
import java.util.List;

public class LockDemo {
    public static void main(String[] args) throws InterruptedException {
        var cache = new CacheService();
        cache.updateRate("EUR/USD", 1.08);

        List<Thread> readers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(()->{
                FxRate rate = cache.getRate("EUR/USD");
                System.out.println("Rate: " + rate.rate());
            });
            thread.start();
            readers.add(thread);
        }
        for (Thread reader : readers) {
            reader.join();
        }
    }
}
