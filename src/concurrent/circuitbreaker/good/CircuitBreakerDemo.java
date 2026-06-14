package concurrent.circuitbreaker.good;

public class CircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== CircuitBreaker [GOOD] — CLOSED → OPEN → HALF_OPEN → CLOSED ==");

        var cb     = new CircuitBreaker(3, 300);
        var client = new PspClient(cb, 3);
        int errors = 0;

        long start = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            try {
                System.out.println("  " + client.charge("pay-" + i, 1000L));
            } catch (CircuitOpenException e) {
                errors++;
                System.out.println("  [fast-fail] " + e.getMessage() + "  state=" + cb.state());
            } catch (RuntimeException e) {
                errors++;
                System.out.println("  [psp-error] " + e.getMessage() + "  state=" + cb.state());
            }

            // После 6-го запроса ждём recovery timeout чтобы circuit перешёл в HALF_OPEN
            if (i == 6) {
                System.out.println("  --- ждём recovery timeout (300ms) ---");
                Thread.sleep(350);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.printf("реальных сетевых вызовов: %d из 10%n", PspClient.totalNetworkCalls.get());
        System.out.printf("суммарное время: %d ms%n", elapsed);
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - fast-fail: запросы при OPEN отклоняются мгновенно, без network timeout");
        System.out.println("  - PSP не получает нагрузку во время восстановления");
        System.out.println("  - автовосстановление через HALF_OPEN — один пробный вызов");
        System.out.println("  - в продакшне: Resilience4j @CircuitBreaker — тот же state machine");
    }
}
