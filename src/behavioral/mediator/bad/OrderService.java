package behavioral.mediator.bad;

import behavioral.mediator.model.InventoryService;
import behavioral.mediator.model.NotificationService;
import behavioral.mediator.model.Order;
import behavioral.mediator.model.PaymentService;
import behavioral.mediator.model.ShippingService;

// Каждый сервис знает всех остальных — связи M-к-M.
// Новый участник = правка OrderService + всех кто его знает.
final class OrderService {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notifier;

    OrderService(InventoryService inventory, PaymentService payment,
                 ShippingService shipping, NotificationService notifier) {
        this.inventory = inventory;
        this.payment   = payment;
        this.shipping  = shipping;
        this.notifier  = notifier;
    }

    void place(Order o) {
        inventory.reserve(o);   // добавить новый шаг? — правь этот метод
        payment.charge(o);      // переставить порядок? — правь этот метод
        shipping.schedule(o);
        notifier.send(o);
    }
}
