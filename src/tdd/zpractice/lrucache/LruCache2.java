package tdd.zpractice.lrucache;


import java.util.HashMap;
import java.util.Map;

public class LruCache2<K, V> {

    private final class Node {
        K key;
        V value;
        Node prev;
        Node next;

        Node() {}
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }

    private final int capacity;
    private final Map<K, Node> data = new HashMap<>();
    private final Node head = new Node();
    private final Node tail = new Node();



    public LruCache2(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public synchronized V get(K key) {
        Node node = data.get(key);
        if (node != null) {
            moveToFront(node);
        }
        return node == null ? null : node.value;
    }

    public synchronized void put(K key, V value) {
        if (data.containsKey(key)) {
            Node existNode = data.get(key);
            existNode.value = value;
            moveToFront(existNode);
            return;
        }

        Node node = new Node(key, value);
        data.put(key, node);
        addFirst(node);
        if (data.size() > capacity) {
            Node lru = tail.prev;
            remove(lru);
            data.remove(lru.key);
        }
    }

    private void addFirst(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        remove(node);
        addFirst(node);
    }


}
