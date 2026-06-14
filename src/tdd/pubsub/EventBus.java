package tdd.pubsub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
                 .add((Consumer<Object>) listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void unsubscribe(Class<T> type, Consumer<T> listener) {
        listeners.getOrDefault(type, List.of()).remove((Consumer<Object>) listener);
    }

    public <T> void publish(T event) {
        listeners.getOrDefault(event.getClass(), List.of()).forEach(listener -> {
            try {
                listener.accept(event);
            } catch (Exception ignored) {
                // один упавший listener не ломает доставку остальным
            }
        });
    }
}
