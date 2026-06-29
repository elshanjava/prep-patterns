package behavioral.mediator.good;

import behavioral.mediator.model.*;

// Вся логика координации собрана здесь; участники знают только посредника.
// Добавить AuditService — реализуй интерфейс и зарегистрируй, участники не меняются.
final class OrderOrchestrator implements OrderMediator {
    private final InventoryService inventory;
    private final PaymentService   payment;
    private final ShippingService  shipping;
    private final NotificationService notifier;
    private final FraudService     fraud;
    // AuditService добавлен: только OrderOrchestrator знает о нём
    private final AuditService     audit;

    OrderOrchestrator(InventoryService inventory, PaymentService payment,
                      ShippingService shipping, NotificationService notifier,
                      FraudService fraud, AuditService audit) {
        this.inventory = inventory;
        this.payment   = payment;
        this.shipping  = shipping;
        this.notifier  = notifier;
        this.fraud     = fraud;
        this.audit     = audit;
    }

    // Все события жизненного цикла заказа обрабатываются здесь.
    // Каждый участник (InventoryService и др.) знает только об OrderMediator,
    // а не обо всех остальных участниках напрямую.
    @Override
    public void notify(String event, Order order) {
        audit.log(event, order);          // аудит каждого события
        switch (event) {
            case "PLACED"          -> {
                fraud.check(order);       // сначала антифрод
                inventory.reserve(order); // затем резервация
            }
            case "FRAUD_CLEARED"   -> payment.charge(order);
            case "PAID"            -> shipping.schedule(order);
            case "PAYMENT_FAILED"  -> {
                inventory.reserve(order); // освободить резерв
                notifier.send(order);     // уведомить клиента об ошибке
            }
            case "SHIPPED"         -> notifier.send(order);
        }
        // Добавить новое событие "DISPUTED" = добавить case здесь.
        // Ни один из участников не меняется.
    }
}
