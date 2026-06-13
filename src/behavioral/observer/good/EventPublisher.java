package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

import java.util.ArrayList;
import java.util.List;

// Аналог Spring ApplicationEventPublisher: издатель не знает подписчиков.
final class EventPublisher {
    private final List<PaymentEventListener> listeners = new ArrayList<>();

    void subscribe(PaymentEventListener listener) { listeners.add(listener); }

    void publish(PaymentCompleted event) {
        listeners.forEach(l -> l.on(event));
    }
}
