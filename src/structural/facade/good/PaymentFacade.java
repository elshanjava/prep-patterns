package structural.facade.good;

import structural.facade.model.*;

import java.math.BigDecimal;

// Фасад: правильная последовательность и состав шагов закреплены в одном месте.
// Все три контроллера делегируют сюда — порядок всегда одинаковый.
// Добавить AML-шаг: правим только PaymentFacade.pay() — ни один контроллер не трогаем.
final class PaymentFacade {
    private final FraudCheck fraudCheck = new FraudCheck();
    private final FxService  fxService  = new FxService();
    private final PspClient  pspClient  = new PspClient();
    private final Ledger     ledger     = new Ledger();
    private final Notifier   notifier   = new Notifier();

    // fraud → fx → psp → ledger → notify: единственное место где это задокументировано кодом
    PaymentResult pay(Order order) {
        fraudCheck.validate(order);
        var rate = fxService.rate(order.currency());
        var auth = pspClient.authorize(order, rate);
        ledger.record(auth);
        notifier.send(order.customer(), auth);
        return PaymentResult.of(auth);
    }

    // psp → ledger → notify: рефанд без FX и без нового fraud-check
    PaymentResult refund(String customer, String authId, long amountCents) {
        var auth = pspClient.refund(authId, amountCents);
        ledger.record(auth);
        notifier.sendRefund(customer, auth);
        return PaymentResult.of(auth);
    }

    // fx → psp → ledger: сверка без уведомления
    void reconcile(Order order) {
        var rate = fxService.rate(order.currency());
        var auth = pspClient.authorize(order, rate);
        ledger.record(auth);
    }
}
