package behavioral.observer;

interface PaymentEventListener {
    void on(PaymentCompleted event);
}
