package tdd.lrucache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Потокобезопасный LRU-кэш фиксированной ёмкости.
 *
 * Вся механика вытеснения — два аргумента LinkedHashMap:
 *   accessOrder = true      -> get() ПЕРЕСТАВЛЯЕТ запись в конец (порядок обращений, не вставок)
 *   removeEldestEntry(...)  -> вызывается после каждого put; true = выкинуть самый старый
 *
 * Почему synchronized, а не ReadWriteLock: при accessOrder = true метод get() МУТИРУЕТ
 * структуру — он переставляет запись. То есть "читателей" здесь нет вообще, все операции
 * пишущие, и разделяемый read lock дал бы гонку, а не выигрыш. Тот редкий случай, когда
 * read-heavy нагрузка не переводится на ReadWriteLock.
 */
public class LruCache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    /** Возвращает значение и делает ключ самым свежим. null, если ключа нет. */
    public synchronized V get(K key) {
        return map.get(key);
    }

    /** Кладёт значение; при переполнении вытесняет самый давно не используемый ключ. */
    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    public synchronized int size() {
        return map.size();
    }

    /** В отличие от get(), НЕ влияет на порядок — LinkedHashMap переставляет только на get/put. */
    public synchronized boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /** Ключи от самого давнего к самому свежему. Нужен тестам, чтобы проверять порядок. */
    public synchronized List<K> keysOldestFirst() {
        return new ArrayList<>(map.keySet());
    }
}
