package behavioral.mediator.bad;

import behavioral.mediator.model.InventoryService;
import behavioral.mediator.model.NotificationService;
import behavioral.mediator.model.Order;
import behavioral.mediator.model.PaymentService;
import behavioral.mediator.model.ShippingService;

// 5 сервисов знают 4 других = до 20 прямых связей (M×(M-1) при M=5).
// Каждый сервис — отдельная зависимость в конструкторе.
// Добавить AML-сервис = правь конструктор + все тесты OrderService.
final class OrderService {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notifier;
    // 5-я зависимость: FraudService добавлен прямо сюда
    private final FraudService fraud;

    OrderService(InventoryService inventory, PaymentService payment,
                 ShippingService shipping, NotificationService notifier,
                 FraudService fraud) {
        this.inventory = inventory;
        this.payment   = payment;
        this.shipping  = shipping;
        this.notifier  = notifier;
        this.fraud     = fraud;
    }

    void place(Order o) {
        // Добавить новый шаг = расширить этот метод.
        // Переставить порядок (сначала fraud, потом inventory) = редактировать этот метод.
        // Добавить ветвление «если fraud.block() → не выполнять shipping» = снова сюда.
        fraud.check(o);         // 1. антифрод
        inventory.reserve(o);   // 2. резервация склада
        payment.charge(o);      // 3. списание
        shipping.schedule(o);   // 4. доставка
        notifier.send(o);       // 5. уведомление

        // Добавить AML = ещё 1 поле + ещё 1 вызов здесь + правка конструктора + правка всех тестов.
        // У каждого сервиса есть «тихое» знание о том, что он вызывается в определённом порядке.
    }
}
