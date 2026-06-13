package behavioral.mediator.good;

import behavioral.mediator.model.*;

public class MediatorDemo {
    public static void main(String[] args) {
        System.out.println("== Mediator [GOOD] ==");

        // Все 6 сервисов знают только посредника — не друг друга.
        // Добавить AuditService = просто передать его в конструктор Orchestrator.
        // InventoryService, PaymentService и т.д. не меняются.
        OrderMediator orchestrator = new OrderOrchestrator(
                new InventoryService(),
                new PaymentService(),
                new ShippingService(),
                new NotificationService(),
                new FraudService(),
                new AuditService()    // новый участник: OrderService в bad/ пришлось бы менять
        );

        var order = new Order("ord-1", 9900);

        // --- полный жизненный цикл заказа (5 событий) ---
        System.out.println("-- полный жизненный цикл --");
        orchestrator.notify("PLACED",        order);
        orchestrator.notify("FRAUD_CLEARED", order);
        orchestrator.notify("PAID",          order);
        orchestrator.notify("SHIPPED",       order);

        // --- сценарий: платёж упал ---
        System.out.println();
        System.out.println("-- сценарий: PAYMENT_FAILED --");
        var failedOrder = new Order("ord-2", 50000);
        orchestrator.notify("PLACED",          failedOrder);
        orchestrator.notify("PAYMENT_FAILED",  failedOrder);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - unit-тест OrderOrchestrator требует моков только для посредника");
        System.out.println("  - добавить AuditService = 1 поле + передать в конструктор, участники не трогаются");
        System.out.println("  - новое событие 'DISPUTED' = 1 case в switch, никакого ripple-effect");
        System.out.println("  - связи: 6 сервисов → 1 посредник (а не 6×5 = 30 прямых зависимостей)");
    }
}
