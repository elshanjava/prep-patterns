package structural.facade.good;

import structural.facade.model.*;

import java.util.Currency;

// Фасад: инкапсулирует правильную последовательность вызовов пяти подсистем.
// Клиент вызывает один метод — не знает об антифроде, FX-курсе, PSP, Ledger, Notifier.
// Добавить аудит-лог: правим только этот класс, а не каждый контроллер.
final class PaymentFacade {
    private final FraudCheck fraudCheck = new FraudCheck();
    private final FxService  fxService  = new FxService();
    private final PspClient  pspClient  = new PspClient();
    private final Ledger     ledger     = new Ledger();
    private final Notifier   notifier   = new Notifier();

    PaymentResult pay(Order order) {
        // Правильный порядок закреплён в одном месте: антифрод → FX → PSP → Ledger → Notify
        fraudCheck.validate(order);
        var rate = fxService.rate(order.currency());
        var auth = pspClient.authorize(order, rate);
        ledger.record(auth);
        notifier.send(order.customer(), auth);
        return PaymentResult.of(auth);
    }
}
