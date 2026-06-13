package structural.facade.bad;

import structural.facade.model.*;

// Контроллер напрямую оркестрирует 5 подсистем.
// Каждый контроллер (PaymentController, RefundController, ReconciliationController...)
// копипастит ту же последовательность вызовов.
// Добавить шаг «запись в аудит-лог»? — правь ВСЕ контроллеры.
// Порядок шагов критичен (антифрод ДО авторизации) — это нигде не закреплено.
final class BadPaymentController {
    // знает о всех 5 подсистемах напрямую
    private final FraudCheck fraudCheck = new FraudCheck();
    private final FxService  fxService  = new FxService();
    private final PspClient  pspClient  = new PspClient();
    private final Ledger     ledger     = new Ledger();
    private final Notifier   notifier   = new Notifier();

    PaymentResult pay(Order order) {
        // 5 шагов видны каждому клиенту; порядок не инкапсулирован
        fraudCheck.validate(order);
        var rate = fxService.rate(order.currency());
        var auth = pspClient.authorize(order, rate);
        ledger.record(auth);
        notifier.send(order.customer(), auth);
        return PaymentResult.of(auth);
    }

    // Для рефанда — придётся снова написать похожую цепочку из 5 вызовов
    // (или скопировать и немного поправить)
}
