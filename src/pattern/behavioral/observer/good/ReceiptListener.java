package pattern.behavioral.observer.good;

import pattern.behavioral.observer.model.PaymentCompleted;

// Добавляется независимо — PaymentService не меняется (OCP).
final class ReceiptListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("receipt: sent for " + e.paymentId() + ", amount=" + e.amount());
    }
}
