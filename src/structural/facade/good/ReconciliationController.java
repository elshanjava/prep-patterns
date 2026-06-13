package structural.facade.good;

import structural.facade.model.Order;

final class ReconciliationController {
    private final PaymentFacade facade = new PaymentFacade();

    void reconcile(Order order) {
        facade.reconcile(order);
    }
}
