package tdd.zpractice.pubsub;

import java.util.*;
import java.util.function.Consumer;

public class EventBus2 {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>())
                .add((Consumer<Object>) listener);
    }

    public <T> void publish(T event) {
        var consumers = listeners.get(event.getClass());
        if (consumers != null) {
            consumers.forEach(listener-> listener.accept(event));
        }
    }

}
