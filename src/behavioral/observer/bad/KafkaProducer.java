package behavioral.observer.bad;

final class KafkaProducer {
    void publish(String topic, String orderId, long amount) {
        System.out.println("kafka: published to " + topic + " orderId=" + orderId);
    }
}
