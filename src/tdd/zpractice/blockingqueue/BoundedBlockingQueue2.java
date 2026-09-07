package tdd.zpractice.blockingqueue;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue2<T> {

    private Object[] items;
    private int head;
    private int tail;
    private int count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public BoundedBlockingQueue2(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        items = new Object[capacity];

    }

    public void put(T a) throws InterruptedException {
        if (a == null) throw new NullPointerException();
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[tail] = a;
            tail = (tail + 1) % items.length;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        T item;
        try {
            while (count == 0) {
                notEmpty.await();
            }
            item = (T) items[head];
            items[head] = null;
            head = (head + 1) % items.length;
            count--;
            notFull.signal();
        } finally {
            lock.unlock();
        }

        return item;
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }


}
