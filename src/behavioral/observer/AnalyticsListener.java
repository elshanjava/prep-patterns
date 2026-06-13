package behavioral.observer;

// В Spring: @TransactionalEventListener(AFTER_COMMIT) — срабатывает только после коммита,
// чтобы не трекать платёж, который откатится.
final class AnalyticsListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("analytics: tracked " + e.paymentId());
    }
}
