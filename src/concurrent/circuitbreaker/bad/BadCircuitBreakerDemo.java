package concurrent.circuitbreaker.bad;

public class BadCircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== CircuitBreaker [BAD] — вызываем PSP даже когда он упал ==");

        var client = new BadPspClient(3); // PSP падает после 3-го вызова
        long start = System.currentTimeMillis();
        int errors = 0;

        for (int i = 1; i <= 10; i++) {
            try {
                System.out.println("  " + client.charge("pay-" + i, 1000L));
            } catch (RuntimeException e) {
                errors++;
                System.out.println("  [error] " + e.getMessage());
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.printf("всего вызовов: %d, ошибок: %d%n", BadPspClient.totalCalls.get(), errors);
        System.out.printf("суммарное время: %d ms%n", elapsed);
        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - 7 вызовов к упавшему PSP × 100ms timeout = 700ms потрачено впустую");
        System.out.println("  - downstream сервисы деградируют пока ждут этих 700ms");
        System.out.println("  - PSP под дополнительной нагрузкой во время восстановления");
        System.out.println("  - нет автоматического восстановления: нужен ручной деплой или перезапуск");
    }
}
