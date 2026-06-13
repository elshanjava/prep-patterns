package behavioral.iterator.bad;

import behavioral.iterator.model.Transaction;

// Клиент завязан на то, что внутри — массив.
// Смена на List/дерево/постраничную подгрузку сломает весь клиентский код.
final class BadTransactionBatch {
    private final Transaction[] items;

    BadTransactionBatch(Transaction... items) { this.items = items; }

    public Transaction[] internalArray() { return items; }  // течёт реализация
}
