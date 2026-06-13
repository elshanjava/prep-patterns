package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

// Добавляется независимо — PaymentService не меняется (OCP).
final class ReceiptListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("receipt: sent for " + e.paymentId() + ", amount=" + e.amount());
    }
}
