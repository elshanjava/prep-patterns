package behavioral.state.good;

public class StateDemo {
    public static void main(String[] args) {
        System.out.println("== State [GOOD] ==");

        // --- Полный жизненный цикл: PENDING → AUTHORIZED → CAPTURED → REFUNDED ---
        System.out.println("-- полный жизненный цикл --");
        var payment = new Payment();
        System.out.println("initial:       " + payment.status());  // Pending
        payment.capture();
        System.out.println("after capture: " + payment.status());  // Authorized
        payment.capture();
        System.out.println("after capture: " + payment.status());  // Captured
        payment.refund();
        System.out.println("after refund:  " + payment.status());  // Refunded

        // --- Недопустимые переходы ---
        System.out.println();
        System.out.println("-- недопустимые переходы --");
        var p2 = new Payment();
        try { p2.refund(); }
        catch (IllegalStateException e) { System.out.println("blocked: " + e.getMessage()); }

        var p3 = new Payment();
        p3.capture();  // → Authorized
        p3.capture();  // → Captured
        try { p3.capture(); }
        catch (IllegalStateException e) { System.out.println("blocked: " + e.getMessage()); }

        var p4 = new Payment();
        p4.capture();
        p4.capture();
        p4.refund();
        try { p4.refund(); }
        catch (IllegalStateException e) { System.out.println("blocked: " + e.getMessage()); }

        // --- Cancel: только из PENDING и AUTHORIZED ---
        System.out.println();
        System.out.println("-- cancel --");
        var p5 = new Payment();
        p5.cancel();
        System.out.println("cancel on Pending: " + p5.status());

        var p6 = new Payment();
        p6.capture();
        p6.cancel();
        System.out.println("cancel on Authorized: " + p6.status());

        var p7 = new Payment();
        p7.capture();
        p7.capture();  // → Captured
        try { p7.cancel(); }
        catch (IllegalStateException e) { System.out.println("blocked cancel on Captured: " + e.getMessage()); }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - добавить DISPUTED = 1 файл Disputed.java implements PaymentState");
        System.out.println("  - Payment, Pending, Authorized, Captured, Refunded — не трогаются");
        System.out.println("  - каждый класс знает только о СВОИХ переходах: легко читать и тестировать");
        System.out.println("  - нет switch'ей: забыть кейс физически невозможно — компилятор проверяет");
    }
}
