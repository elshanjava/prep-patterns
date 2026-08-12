package concurrent.circuitbreaker.good;

public class CircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== CircuitBreaker [GOOD] — CLOSED → OPEN → HALF_OPEN → CLOSED ==");

        var cb     = new CircuitBreaker(3, 300);
        var client = new PspClient(cb, 3, 1200);   // PSP лежит, но через 1200ms оживает
        int errors = 0;

        long start = System.currentTimeMillis();

        for (int i = 1; i <= 12; i++) {
            try {
                System.out.println("  " + i + ") " + client.charge("pay-" + i, 1000L)
                                 + "   state=" + cb.state());
            } catch (CircuitOpenException e) {
                errors++;
                System.out.println("  " + i + ") [fast-fail] " + e.getMessage() + "  state=" + cb.state());
            } catch (RuntimeException e) {
                errors++;
                System.out.println("  " + i + ") [psp-error] " + e.getMessage() + "  state=" + cb.state());
            }

            // Ждём recovery timeout, чтобы цепь ушла в HALF_OPEN и отправила пробу.
            // Первая проба (после 6) упадёт — PSP ещё лежит, вернёмся в OPEN.
            // Вторая (после 8) пройдёт — PSP уже ожил, цепь замкнётся в CLOSED.
            if (i == 6 || i == 8) {
                System.out.println("  --- ждём recovery timeout (300ms) ---");
                Thread.sleep(350);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.printf("реальных сетевых вызовов: %d из 12, ошибок: %d%n",
                          PspClient.totalNetworkCalls.get(), errors);
        System.out.printf("суммарное время: %d ms%n", elapsed);
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - fast-fail: запросы при OPEN отклоняются мгновенно, без network timeout");
        System.out.println("  - PSP не получает нагрузку во время восстановления");
        System.out.println("  - автовосстановление через HALF_OPEN — один пробный вызов");
        System.out.println("  - проба упала -> снова OPEN и новое окно ожидания;");
        System.out.println("    проба прошла -> CLOSED, трафик пошёл (видно на запросах 7 и 9)");
        System.out.println("  - в продакшне: Resilience4j @CircuitBreaker — тот же state machine");
    }
}
