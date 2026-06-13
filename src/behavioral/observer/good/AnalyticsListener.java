package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

// В Spring: @TransactionalEventListener(AFTER_COMMIT) — только после коммита транзакции.
final class AnalyticsListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("analytics: tracked " + e.paymentId());
    }
}
