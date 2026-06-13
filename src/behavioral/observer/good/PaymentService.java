package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

// Издатель: не знает о подписчиках — публикует факт и уходит.
final class PaymentService {
    private final EventPublisher publisher;

    PaymentService(EventPublisher publisher) { this.publisher = publisher; }

    void pay(String orderId, long amount) {
        System.out.println("payment: processing " + orderId);
        publisher.publish(new PaymentCompleted(orderId, amount));
    }
}
