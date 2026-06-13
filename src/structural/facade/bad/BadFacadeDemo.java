package structural.facade.bad;

import structural.facade.model.Order;

import java.util.Currency;

public class BadFacadeDemo {
    public static void main(String[] args) {
        System.out.println("== Facade [BAD] ==");

        var controller = new BadPaymentController();
        var order = new Order("cust-1", Currency.getInstance("USD"), 4999);
        System.out.println("result: " + controller.pay(order));

        System.out.println();
        System.out.println("Добавить шаг 'AML-проверка' между fraud и fx:");
        System.out.println("  → найди каждый контроллер с такой цепочкой и вставь вызов.");
        System.out.println("Порядок 'fraud перед psp' нигде явно не задокументирован.");
    }
}
