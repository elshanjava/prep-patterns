package coding.behavioral.observer;

public class OrderService {
    private final OrderPublisher orderPublisher;

    public OrderService(OrderPublisher orderPublisher) {
        this.orderPublisher = orderPublisher;
    }

    public void publishOrderStatus(Order order, String status) {
        order.setStatus(status);
        orderPublisher.publish(order);
    }
}
