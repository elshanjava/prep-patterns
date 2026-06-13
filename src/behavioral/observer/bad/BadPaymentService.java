package behavioral.observer.bad;

// Издатель знает всех потребителей поимённо.
// Новый потребитель = правка этого класса (нарушение OCP).
final class BadPaymentService {
    private final EmailSender email;
    private final Analytics analytics;
    private final LedgerService ledger;

    BadPaymentService(EmailSender email, Analytics analytics, LedgerService ledger) {
        this.email     = email;
        this.analytics = analytics;
        this.ledger    = ledger;
    }

    void pay(String orderId, long amount) {
        System.out.println("payment: processing " + orderId);
        email.sendReceipt(orderId, amount);   // добавить нового потребителя =
        analytics.track(orderId, amount);     // правка этого класса
        ledger.record(orderId, amount);
    }
}
