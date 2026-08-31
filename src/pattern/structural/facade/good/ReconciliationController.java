package pattern.structural.facade.good;

import pattern.structural.facade.model.Order;

final class ReconciliationController {
    private final PaymentFacade facade = new PaymentFacade();

    void reconcile(Order order) {
        facade.reconcile(order);
    }
}
