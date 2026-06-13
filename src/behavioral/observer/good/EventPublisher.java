package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

import java.util.ArrayList;
import java.util.List;

// Аналог Spring ApplicationEventPublisher: издатель не знает подписчиков.
// subscribe/unsubscribe позволяют динамически менять набор слушателей.
final class EventPublisher {
    private final List<PaymentEventListener> listeners = new ArrayList<>();

    void subscribe(PaymentEventListener listener)   { listeners.add(listener);    }
    void unsubscribe(PaymentEventListener listener) { listeners.remove(listener); }

    void publish(PaymentCompleted event) {
        listeners.forEach(l -> l.on(event));
    }
}
