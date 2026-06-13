package behavioral.iterator.bad;

import behavioral.iterator.model.Transaction;

// Клиент завязан на то, что внутри — массив.
// Смена на List/дерево/постраничную подгрузку сломает весь клиентский код.
final class BadTransactionBatch {
    private final Transaction[] items;

    BadTransactionBatch(Transaction... items) { this.items = items; }

    // Течёт реализация — клиент знает тип хранилища
    public Transaction[] internalArray() { return items; }

    // Дополнительные методы, дублирующие знание о массиве:
    // Смена на List → getByIndex перестанет компилироваться у всех вызывающих
    public Transaction getByIndex(int i) {
        if (i < 0 || i >= items.length)
            throw new IndexOutOfBoundsException("index " + i + " out of bounds for length " + items.length);
        return items[i];
    }

    // size() как прямое свойство массива — клиент ориентируется на него в циклах
    public int size() { return items.length; }

    // Если завтра данные придут из БД страницами, этот контракт (массив + size + getByIndex)
    // придётся менять в клиентском коде в 3+ местах (см. BadIteratorDemo)
}
