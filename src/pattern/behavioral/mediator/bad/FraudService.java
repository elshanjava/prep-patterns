package pattern.behavioral.mediator.bad;

import pattern.behavioral.mediator.model.Order;

// Пятый сервис, добавленный как прямая зависимость OrderService.
// Каждый новый сервис увеличивает fan-out конструктора.
final class FraudService {
    void check(Order o) {
        System.out.println("fraud: checked order " + o.id() + " amount=" + o.amount());
    }
}
