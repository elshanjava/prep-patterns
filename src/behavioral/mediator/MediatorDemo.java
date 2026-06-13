package behavioral.mediator;

public class MediatorDemo {
    public static void main(String[] args) {
        System.out.println("== Mediator ==");

        OrderMediator orchestrator = new OrderOrchestrator(
                new InventoryService(),
                new PaymentService(),
                new ShippingService(),
                new NotificationService()
        );

        var order = new Order("ord-1", 9900);
        orchestrator.notify("PLACED",  order);   // reserve + charge
        orchestrator.notify("PAID",    order);   // schedule shipping
        orchestrator.notify("SHIPPED", order);   // send notification
    }
}
