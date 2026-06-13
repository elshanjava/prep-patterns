package behavioral.mediator.good;

import behavioral.mediator.model.Order;

interface OrderMediator {
    void notify(String event, Order order);
}
