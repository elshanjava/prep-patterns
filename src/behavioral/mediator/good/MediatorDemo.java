package behavioral.mediator.good;

import behavioral.mediator.model.*;

public class MediatorDemo {
    public static void main(String[] args) {
        System.out.println("== Mediator [GOOD] ==");

        OrderMediator orchestrator = new OrderOrchestrator(
                new InventoryService(), new PaymentService(),
                new ShippingService(), new NotificationService()
        );

        var order = new Order("ord-1", 9900);
        orchestrator.notify("PLACED",  order);
        orchestrator.notify("PAID",    order);
        orchestrator.notify("SHIPPED", order);
    }
}
