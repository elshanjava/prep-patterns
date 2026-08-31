package pattern.behavioral.mediator.good;

import pattern.behavioral.mediator.model.Order;

interface OrderMediator {
    void notify(String event, Order order);
}
