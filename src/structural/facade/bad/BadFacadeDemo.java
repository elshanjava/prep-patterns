package structural.facade.bad;

import structural.facade.model.Order;

import java.util.Currency;

public class BadFacadeDemo {
    public static void main(String[] args) {
        System.out.println("== Facade [BAD] — одна и та же оркестрация в трёх контроллерах ==");
        System.out.println();

        var order = new Order("user@example.com", Currency.getInstance("USD"), 10_000L);

        System.out.println("--- BadPaymentController.pay() ---");
        System.out.println("  шаги: fraud → fx → psp → ledger → notify");
        new BadPaymentController().pay(order);

        System.out.println();
        System.out.println("--- BadRefundController.refund() ---");
        System.out.println("  шаги: psp → ledger → notify  (fraud пропущен! намеренно или нет?)");
        new BadRefundController().refund("user@example.com", "auth_user@example.com", 5_000L);

        System.out.println();
        System.out.println("--- BadReconciliationController.reconcile() ---");
        System.out.println("  шаги: fx → psp → ledger  (notify не нужен? или забыли?)");
        new BadReconciliationController().reconcile(order);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Добавить AML-шаг между fraud и fx:");
        System.out.println("    → BadPaymentController      ← правь здесь");
        System.out.println("    → BadRefundController       ← и здесь");
        System.out.println("    → BadReconciliationController ← и здесь");
        System.out.println("  Порядок fraud→fx→psp никак не закреплён — в каждом контроллере свой");
        System.out.println("  Тест: нельзя проверить «правильный порядок» без запуска каждого контроллера");
    }
}
