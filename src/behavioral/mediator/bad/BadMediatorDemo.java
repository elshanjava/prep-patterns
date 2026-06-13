package behavioral.mediator.bad;

import behavioral.mediator.model.*;

public class BadMediatorDemo {
    public static void main(String[] args) {
        System.out.println("== Mediator [BAD] ==");

        // Конструктор принимает 5 сервисов — граф зависимостей сразу виден как проблема.
        // Добавить AML = 6-й параметр сюда + поле в OrderService + правка всех тестов.
        var service = new OrderService(
                new InventoryService(),
                new PaymentService(),
                new ShippingService(),
                new NotificationService(),
                new FraudService()              // 5-я зависимость
        );

        service.place(new Order("ord-1", 9900));

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - 5 зависимостей в конструкторе: 5×4 = 20 потенциальных прямых связей");
        System.out.println("  - добавить AML = правь OrderService.place() + конструктор + все тесты");
        System.out.println("  - переставить шаги (fraud до inventory) = хирургия в place()");
        System.out.println("  - unit-тест OrderService требует 5 моков вместо 1");
    }
}
