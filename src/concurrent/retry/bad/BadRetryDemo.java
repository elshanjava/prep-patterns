package concurrent.retry.bad;

public class BadRetryDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Retry [BAD] — фиксированная задержка, thundering herd ==");

        var client = new BadPspClient(2); // первые 2 попытки падают

        long start = System.currentTimeMillis();
        try {
            System.out.println("  result: " + client.charge("pay-1", 5000L));
        } catch (RuntimeException e) {
            System.out.println("  failed: " + e.getMessage());
        }
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%nпопыток: %d, время: %d ms%n", client.attempts.get(), elapsed);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - задержки: 100ms, 100ms, 100ms — все клиенты retryят в один момент");
        System.out.println("  - 1000 клиентов упали одновременно → 1000 retry ровно через 100ms → второй пик");
        System.out.println("  - нет ограничения на суммарное время ожидания (max delay)");
        System.out.println("  - нет jitter: коллизии гарантированы при одновременных сбоях");
    }
}
