package structural.facade.bad;

import structural.facade.model.*;

import java.util.Currency;

// Дублирует часть шагов из BadPaymentController — но без антифрода!
// «Проверку fraud при рефанде делать не нужно» — решили на ходу, нигде не задокументировано.
// Когда в BadPaymentController добавят AML-шаг, сюда забудут добавить.
final class BadRefundController {
    private final PspClient pspClient = new PspClient();
    private final Ledger    ledger    = new Ledger();
    private final Notifier  notifier  = new Notifier();
    // FraudCheck — забыт! или намеренно? Непонятно.

    PaymentResult refund(String customer, String authId, long amountCents) {
        // порядок шагов не задокументирован — может отличаться от контроллера к контроллеру
        var auth = pspClient.refund(authId, amountCents);
        ledger.record(auth);
        notifier.sendRefund(customer, auth);
        return PaymentResult.of(auth);
    }
}
