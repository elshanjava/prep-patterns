package behavioral.state.bad;

public class BadStateDemo {
    public static void main(String[] args) {
        System.out.println("== State [BAD] ==");

        // --- Полный жизненный цикл ---
        System.out.println("-- полный жизненный цикл: PENDING → AUTHORIZED → CAPTURED → REFUNDED --");
        var payment = new BadPayment();
        System.out.println("initial:       " + payment.status());
        payment.capture();
        System.out.println("after capture: " + payment.status());
        // Теперь нужен шаг CAPTURED — для этого пришлось добавить CAPTURED в 3 switch'а
        // В реальной PSP-системе: AUTHORIZED → CAPTURED → REFUNDED (3-шаговый цикл)
        payment.refund();
        System.out.println("after refund:  " + payment.status());

        // --- Недопустимые переходы ---
        System.out.println();
        System.out.println("-- недопустимые переходы --");
        var p2 = new BadPayment();
        try { p2.refund(); }
        catch (IllegalStateException e) { System.out.println("blocked refund on PENDING: " + e.getMessage()); }

        var p3 = new BadPayment();
        p3.capture();
        try { p3.capture(); }
        catch (IllegalStateException e) { System.out.println("blocked capture on AUTHORIZED: " + e.getMessage()); }

        // --- Отмена ---
        System.out.println();
        System.out.println("-- cancel --");
        var p4 = new BadPayment();
        p4.cancel();
        System.out.println("cancel on PENDING: " + p4.status());

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - добавить статус DISPUTED = правь switch в capture() + refund() + cancel()");
        System.out.println("  - 3 switch'а × 4 статуса = 12 кейсов: пропустить 1 = молчаливый баг");
        System.out.println("  - capture() и refund() знают обо ВСЕХ статусах, хотя каждый обрабатывает 1-2");
        System.out.println("  - тест перехода AUTHORIZED→CAPTURED требует понимания ВСЕХ трёх методов");
    }
}
