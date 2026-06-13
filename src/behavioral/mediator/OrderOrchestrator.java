package behavioral.mediator;

// Вся логика координации «кто за кем» собрана здесь; участники знают только посредника.
final class OrderOrchestrator implements OrderMediator {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notifier;

    OrderOrchestrator(InventoryService inventory, PaymentService payment,
                      ShippingService shipping, NotificationService notifier) {
        this.inventory = inventory;
        this.payment = payment;
        this.shipping = shipping;
        this.notifier = notifier;
    }

    public void notify(String event, Order order) {
        switch (event) {
            case "PLACED"  -> { inventory.reserve(order); payment.charge(order); }
            case "PAID"    -> shipping.schedule(order);
            case "SHIPPED" -> notifier.send(order);
        }
    }
}
