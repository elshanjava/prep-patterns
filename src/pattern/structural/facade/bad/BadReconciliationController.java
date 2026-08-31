package pattern.structural.facade.bad;

import pattern.structural.facade.model.FxService;
import pattern.structural.facade.model.Ledger;
import pattern.structural.facade.model.Order;
import pattern.structural.facade.model.PspClient;

// Ещё один контроллер с частичной копией оркестрации.
// Порядок fx → psp → ledger задублирован в третий раз.
// Добавить шаг «AML-отчёт»? — правь BadPaymentController, BadRefundController, здесь.
final class BadReconciliationController {
    private final FxService fxService = new FxService();
    private final PspClient pspClient = new PspClient();
    private final Ledger ledger    = new Ledger();
    // Notifier не нужен при сверке — но кто это знает? Порядок нигде не закреплён.

    void reconcile(Order order) {
        var rate = fxService.rate(order.currency());
        var auth = pspClient.authorize(order, rate);
        ledger.record(auth);   // повторная запись при сверке — идемпотентна?
    }
}
