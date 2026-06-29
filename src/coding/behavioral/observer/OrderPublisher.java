package coding.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

public class OrderPublisher {
    private final List<OrderListener> orderListeners = new ArrayList<>();

    public void subscribe(OrderListener orderListener) {
        orderListeners.add(orderListener);
    }

    public void unsubscribe(OrderListener orderListener) {
        orderListeners.remove(orderListener);
    }

    public void publish(Order order) {
        orderListeners.forEach(o -> o.onStatusChanged(order));
    }
}
