package tdd.zpractice.blockingqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue2<T> {

  private Object[] items;
  private int head;
  private int tail;
  private int count;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition notFull = lock.newCondition();
  private final Condition notEmpty = lock.newCondition();

  public BoundedBlockingQueue2(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity should be larger then 0");
    }
    this.items = new Object[capacity];
  }

  public void put(T object) throws InterruptedException {
    lock.lock();
    try {
      while (count == items.length) {
        notFull.await();
      }
      enqueue(object);
    } finally {
      lock.unlock();
    }
  }


  @SuppressWarnings("unchecked")
  public T take() throws InterruptedException {
    lock.lock();
    try {
      while (count==0) {
        notEmpty.await();
      }
      return dequeue();
    } finally {
      lock.unlock();
    }
  }

  public int size() {
    return count;
  }

  private T dequeue() {
    T item = (T)items[head];
    count--;
    items[head] = null;
    head = (head + 1) % items.length;
    notFull.signal();
    return item;
  }

  private void enqueue(T object) {
    items[tail] = object;
    tail = (tail + 1) % items.length;
    count++;
    notEmpty.signal();
  }

}
