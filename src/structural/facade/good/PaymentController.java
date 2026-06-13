package structural.facade.good;

import structural.facade.model.Order;
import structural.facade.model.PaymentResult;

// Контроллер знает только о PaymentFacade — не о пяти подсистемах.
// RefundController, ReconciliationController — также используют фасад.
final class PaymentController {
    private final PaymentFacade facade = new PaymentFacade();

    PaymentResult pay(Order order) {
        return facade.pay(order);
    }
}
