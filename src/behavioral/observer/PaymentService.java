package behavioral.observer;

// Издатель: не знает о подписчиках — публикует факт оплаты и уходит.
final class PaymentService {
    private final EventPublisher publisher;

    PaymentService(EventPublisher publisher) { this.publisher = publisher; }

    void pay(String orderId, long amount) {
        System.out.println("payment: processing " + orderId);
        publisher.publish(new PaymentCompleted(orderId, amount));
    }
}
