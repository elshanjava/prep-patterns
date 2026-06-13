package structural.facade.good;

import structural.facade.model.Order;

import java.util.Currency;

public class FacadeDemo {
    public static void main(String[] args) {
        System.out.println("== Facade [GOOD] — три контроллера, одна оркестрация ==");
        System.out.println();

        var order = new Order("user@example.com", Currency.getInstance("USD"), 10_000L);

        System.out.println("--- PaymentController.pay() ---");
        var result = new PaymentController().pay(order);
        System.out.println("  result: " + result);

        System.out.println();
        System.out.println("--- RefundController.refund() ---");
        var refund = new RefundController().refund("user@example.com", "auth_user@example.com", 5_000L);
        System.out.println("  result: " + refund);

        System.out.println();
        System.out.println("--- ReconciliationController.reconcile() ---");
        new ReconciliationController().reconcile(order);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  Добавить AML-шаг: правим только PaymentFacade.pay() — 1 метод, 1 класс");
        System.out.println("  Порядок fraud→fx→psp→ledger→notify задокументирован кодом в одном месте");
        System.out.println("  Тест оркестрации: тестируем PaymentFacade один раз, не 3 контроллера");
        System.out.println("  PaymentController/RefundController/ReconciliationController: по 1-2 строки");
    }
}
