package tdd.zpractice.blockingqueue;


public class BoundedBlockingQueue2<T> {

  private Object[] items;
  private int head;
  private int tail;

  public BoundedBlockingQueue2(int capacity) {
    items = new Object[capacity];

  }

  public void put(T a) {
    items[tail] = a;
    tail = (tail + 1);
  }

  @SuppressWarnings("unchecked")
  public T take() {
    T item = (T)items[head];
    items[head] = null;
    head = (head + 1);
    return item;
  }
}
