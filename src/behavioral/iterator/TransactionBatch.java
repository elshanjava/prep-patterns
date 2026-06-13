package behavioral.iterator;

import java.util.Iterator;
import java.util.List;

final class TransactionBatch implements Iterable<Transaction> {
    private final List<Transaction> items;

    TransactionBatch(List<Transaction> items) { this.items = items; }

    @Override
    public Iterator<Transaction> iterator() {
        return items.iterator();
    }
}
