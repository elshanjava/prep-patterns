package tdd.blockingqueue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ограниченная блокирующая очередь с нуля — без java.util.concurrent.BlockingQueue.
 * Кольцевой буфер + ReentrantLock + ДВА условия.
 *
 * Почему два Condition, а не один монитор. С synchronized очередь ожидания одна на всех:
 * там спят и продюсеры (ждут места), и консьюмеры (ждут элемента). Разбудить нужного
 * нельзя — notify() может поднять продюсера, когда освободился элемент, тот увидит,
 * что места по-прежнему нет, снова уснёт, и очередь встанет при живых данных. Спасает
 * только notifyAll(), то есть будим всех и заставляем каждого перепроверять.
 * Отдельные notFull/notEmpty позволяют будить ровно тех, кого надо, через signal().
 *
 * Ровно так устроен ArrayBlockingQueue внутри.
 */
public class BoundedBlockingQueue<E> {

    private final Object[] items;
    private int head;      // откуда берём
    private int tail;      // куда кладём
    private int count;

    private final ReentrantLock lock     = new ReentrantLock();
    private final Condition     notFull  = lock.newCondition();
    private final Condition     notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got: " + capacity);
        }
        this.items = new Object[capacity];
    }

    /** Кладёт элемент, блокируясь, пока не появится место. */
    public void put(E e) throws InterruptedException {
        requireNonNull(e);
        lock.lock();
        try {
            // WHILE, а не IF — и это не про ложные пробуждения (хотя и про них тоже).
            // Между signal() и моментом, когда мы реально вернём себе замок, другой
            // продюсер успевает занять освободившееся место. Проснулись — перепроверь.
            while (count == items.length) {
                notFull.await();
            }
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }

    /** Забирает элемент, блокируясь, пока очередь пуста. */
    public E take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    /** Кладёт с ограниченным ожиданием. false — место так и не освободилось. */
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        requireNonNull(e);
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (count == items.length) {
                // awaitNanos ВОЗВРАЩАЕТ остаток. Пересчитывать его обязательно: иначе
                // после каждого ложного пробуждения таймаут отсчитывался бы заново
                // и ожидание могло бы не кончиться никогда.
                if (nanos <= 0) return false;
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(e);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /** Забирает с ограниченным ожиданием. null — не дождались. */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (count == 0) {
                if (nanos <= 0) return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    public int capacity() {
        return items.length;
    }

    // ── внутреннее: вызывается только под захваченным замком ─────────────────

    private void enqueue(E e) {
        items[tail] = e;
        tail = (tail + 1) % items.length;   // кольцо: дошли до конца — заходим с начала
        count++;
        notEmpty.signal();                  // signal, не signalAll: ждут ОДНОРОДНЫЕ потоки,
    }                                       // все на одном условии — хватит разбудить одного

    @SuppressWarnings("unchecked")
    private E dequeue() {
        E e = (E) items[head];
        items[head] = null;                 // обнуляем: иначе массив держит ссылку на
        head = (head + 1) % items.length;   // отданный элемент и не даёт его собрать
        count--;
        notFull.signal();
        return e;
    }

    private static void requireNonNull(Object e) {
        // null занят под "элемента нет" в poll() — хранить его нельзя, иначе
        // не отличить "дождались null" от "не дождались".
        if (e == null) throw new NullPointerException("null не хранится в очереди");
    }
}
