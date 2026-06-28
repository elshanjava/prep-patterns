package coding.behavioral.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Своя коллекция на обычном массиве. Реализует Iterable<T> -> работает в for-each.
public class Playlist<T> implements Iterable<T> {
    private final Object[] items;
    private int size;

    public Playlist(int capacity) {
        this.items = new Object[capacity];
    }

    public void add(T element) {
        items[size++] = element;
    }

    public int size() {
        return size;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int position = 0;
            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if(!hasNext()) throw new NoSuchElementException();
                return (T)items[position++];
            }
        };
    }
}
