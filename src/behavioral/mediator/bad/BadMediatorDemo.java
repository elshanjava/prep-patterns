package behavioral.mediator.bad;

import behavioral.mediator.model.*;

public class BadMediatorDemo {
    public static void main(String[] args) {
        System.out.println("== Mediator [BAD] ==");
        var service = new OrderService(
                new InventoryService(), new PaymentService(),
                new ShippingService(), new NotificationService()
        );
        service.place(new Order("ord-1", 9900));
    }
}
