package behavioral.iterator.bad;

import behavioral.iterator.model.Transaction;

import java.math.BigDecimal;

public class BadIteratorDemo {
    public static void main(String[] args) {
        System.out.println("== Iterator [BAD] ==");

        var batch = new BadTransactionBatch(
                new Transaction("tx-1", new BigDecimal("100")),
                new Transaction("tx-2", new BigDecimal("200")),
                new Transaction("tx-3", new BigDecimal("50"))
        );

        // Стиль 1: индексный цикл через internalArray()
        // Смена на List → batch.internalArray() → ОШИБКА КОМПИЛЯЦИИ
        System.out.println("-- цикл 1: индексный через internalArray() --");
        for (int i = 0; i < batch.internalArray().length; i++) {
            Transaction tx = batch.internalArray()[i];
            System.out.println(tx.id() + ": " + tx.amount());
        }

        // Стиль 2: цикл через size() и getByIndex()
        // Смена на List → batch.getByIndex() → ОШИБКА КОМПИЛЯЦИИ
        System.out.println("-- цикл 2: через size() и getByIndex() --");
        for (int i = 0; i < batch.size(); i++) {
            Transaction tx = batch.getByIndex(i);
            System.out.println(tx.id() + ": " + tx.amount());
        }

        // Стиль 3: for-each по internalArray()
        // Смена на List → batch.internalArray() → ОШИБКА КОМПИЛЯЦИИ
        System.out.println("-- цикл 3: for-each по internalArray() --");
        BigDecimal sum = BigDecimal.ZERO;
        for (Transaction tx : batch.internalArray()) {
            sum = sum.add(tx.amount());
        }
        System.out.println("sum: " + sum);

        // Итого: 3 разных места используют internalArray()/size()/getByIndex().
        // Смена внутренней структуры с Transaction[] на List<Transaction>
        // или на постраничную подгрузку из БД → нужно исправить все 3 цикла.
        // Если забыть про стиль 3 — молчаливый сбой компиляции по другой части проекта.
        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - change to List → исправь все 3 цикла во всех клиентах");
        System.out.println("  - size() + getByIndex() течёт реализация через публичный API");
        System.out.println("  - нет stream-совместимости: фильтрация/агрегация — вручную");
    }
}
