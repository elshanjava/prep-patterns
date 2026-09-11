package tdd.zpractice.lrucache;


import java.util.HashMap;
import java.util.Map;

public class LruCache2<K, V> {

    private class Node {
        K key;
        V value;
        Node prev, next;
        Node() {}                        // для sentinel'ов
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node> data = new HashMap<>();
    private final Node head = new Node();               // dummy
    private final Node tail = new Node();               // dummy


    public LruCache2(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        Node n = data.get(key);
        return n == null ? null : n.value;
    }

    public void put(K key, V value) {
        Node n = new Node(key, value);
        if (data.size() == capacity) {
            data.remove(n.next.key);
        }

        data.put(key, n);
    }
}
