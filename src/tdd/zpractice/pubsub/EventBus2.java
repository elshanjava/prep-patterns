package tdd.zpractice.pubsub;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus2 {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) listener);
    }

    public <T> void publish(T event) {
        var consumers = listeners.get(event.getClass());
        if (consumers != null) {
            consumers.forEach(listener-> {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    // один упавший listener не ломает доставку остальным
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void unsubscribe(Class<T> type, Consumer<T> event) {
        var consumers = listeners.get(type);
        if (consumers != null) consumers.remove((Consumer<Object>)event);
    }
}
