package pattern.behavioral.observer.good;

import pattern.behavioral.observer.model.PaymentCompleted;

// В Spring: @TransactionalEventListener(AFTER_COMMIT) — только после коммита транзакции.
final class AnalyticsListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("analytics: tracked " + e.paymentId());
    }
}
