package structural.facade.good;

import structural.facade.model.Order;

import java.util.Currency;

public class FacadeDemo {
    public static void main(String[] args) {
        System.out.println("== Facade [GOOD] — контроллер не знает о пяти подсистемах ==");

        var controller = new PaymentController();

        var result = controller.pay(new Order(
                "user@example.com",
                Currency.getInstance("USD"),
                10_000L));
        System.out.println("result: " + result);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - PaymentController: 1 вызов вместо 5 шагов × 5 подсистем");
        System.out.println("  - порядок антифрод → FX → PSP → Ledger → Notify закреплён в фасаде");
        System.out.println("  - добавить аудит-лог: один метод в PaymentFacade — не во всех контроллерах");
        System.out.println("  - новый RefundController использует тот же фасад без дублирования");
    }
}
