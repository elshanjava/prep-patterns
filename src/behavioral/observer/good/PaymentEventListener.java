package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

interface PaymentEventListener {
    void on(PaymentCompleted event);
}
