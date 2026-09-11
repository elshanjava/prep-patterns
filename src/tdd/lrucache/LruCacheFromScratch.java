package tdd.lrucache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LRU-кэш «с нуля» — то, что просят на собесе (LeetCode 146).
 *
 * Две структуры работают в паре:
 *   HashMap<K, Node>       — O(1) «найти узел по ключу»
 *   двусвязный список      — O(1) «переставить узел в свежий конец» и «снять самый старый»
 *
 * Почему список, а не дека: LRU на каждом обращении двигает элемент из СЕРЕДИНЫ в голову.
 * Дека — O(1) только на концах; вырезание из середины у неё O(n). Двусвязный список с
 * прямым указателем на узел (его даёт мапа) вырезает за O(1) — узел знает соседей prev/next.
 *
 * Почему Node хранит key: при вытеснении берём хвостовой узел и должны удалить его И из мапы;
 * мапа по ключу, поэтому ключ лежит в узле — map.remove(node.key), без поиска.
 *
 * Почему synchronized, а не ReadWriteLock: get() при LRU МУТИРУЕТ (переставляет узел),
 * то есть «читателей» тут нет — все операции пишущие. Общий read-lock дал бы гонку, а не
 * выигрыш. (Контраст с load balancer, где get() реально только читал список.)
 *
 * sentinel-узлы (head/tail dummy): реальные узлы всегда между ними, поэтому prev/next
 * никогда не null — ни одной проверки на краях, меньше багов.
 */
public class LruCacheFromScratch<K, V> {

    /** Узел двусвязного списка. Нестатический внутренний класс — видит K и V родителя. */
    private final class Node {
        K key;
        V value;
        Node prev, next;

        Node() {}                                  // для sentinel'ов (пустые)
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node> map = new HashMap<>();
    private final Node head = new Node();          // sentinel: сразу за ним — САМЫЙ СВЕЖИЙ
    private final Node tail = new Node();           // sentinel: сразу перед ним — САМЫЙ СТАРЫЙ

    public LruCacheFromScratch(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        head.next = tail;                          // пустой список: head <-> tail
        tail.prev = head;
    }

    /** Возвращает значение и делает ключ самым свежим. null, если ключа нет. */
    public synchronized V get(K key) {
        Node n = map.get(key);
        if (n == null) return null;
        moveToFront(n);                            // обращение освежает → это LRU, а не FIFO
        return n.value;
    }

    /** Кладёт/обновляет; при переполнении вытесняет самый давно не используемый ключ. */
    public synchronized void put(K key, V value) {
        Node existing = map.get(key);
        if (existing != null) {                    // ключ уже есть — обновить значение и освежить
            existing.value = value;
            moveToFront(existing);
            return;                                // размер не растёт, вытеснять нечего
        }
        Node node = new Node(key, value);
        addFirst(node);                            // в свежий конец
        map.put(key, node);

        if (map.size() > capacity) {               // переполнение → снять хвост (LRU)
            Node lru = tail.prev;                  // самый старый — перед tail
            remove(lru);                           // из списка
            map.remove(lru.key);                   // из мапы (вот зачем key в узле)
        }
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /** Ключи от самого старого (у tail) к самому свежему (у head). Нужен тестам для проверки порядка. */
    public synchronized List<K> keysOldestFirst() {
        List<K> keys = new ArrayList<>();
        for (Node n = tail.prev; n != head; n = n.prev) {
            keys.add(n.key);
        }
        return keys;
    }

    // ── операции над списком (все O(1)) ──────────────────────────────────────

    /** Вставить узел сразу после head (в свежий конец). */
    private void addFirst(Node node) {
        node.prev = head;                          // (1) слева от нового — head
        node.next = head.next;                     // (2) справа — бывший первый
        head.next.prev = node;                     // (3) у бывшего первого сосед слева — новый
        head.next = node;                          // (4) у head сосед справа — новый
    }

    /** Выдернуть узел из списка (соседи сцепляются напрямую). */
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /** Переставить существующий узел в свежий конец: вырезать + вставить в голову. */
    private void moveToFront(Node node) {
        remove(node);
        addFirst(node);
    }
}
