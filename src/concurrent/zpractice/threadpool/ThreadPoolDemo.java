package concurrent.zpractice.threadpool;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.LongStream;

public class ThreadPoolDemo {
    public static void main(String[] args) throws Exception{
        List<Long> amounts = LongStream.rangeClosed(1, 20).map(i -> i * 100L).boxed().toList();
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;

        long start = System.currentTimeMillis();

        try (var processor = new PaymentProcessor()) {
            List<Future<String>> futures = processor.processBatch(amounts);
            for (Future<String> f : futures) {
                System.out.println("  " + f.get());
            }
        }
    }
}
