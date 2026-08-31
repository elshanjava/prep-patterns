package pattern.behavioral.observer.good;

import pattern.behavioral.observer.model.PaymentCompleted;

interface PaymentEventListener {
    void on(PaymentCompleted event);
}
