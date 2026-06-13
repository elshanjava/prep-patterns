package behavioral.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

// Ленивый постраничный итератор: подгружает следующую страницу по мере обхода.
final class PagedIterator<T> implements Iterator<T> {
    private final IntFunction<List<T>> loader;
    private final int pageSize;
    private List<T> page;
    private int pageIndex = 0;
    private int pos = 0;

    PagedIterator(IntFunction<List<T>> loader, int pageSize) {
        this.loader = loader;
        this.pageSize = pageSize;
        this.page = loader.apply(pageIndex++);
    }

    public boolean hasNext() {
        return pos < page.size();
    }

    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        T item = page.get(pos++);
        if (pos == page.size() && page.size() == pageSize) {
            page = loader.apply(pageIndex++);
            pos = 0;
        }
        return item;
    }
}
