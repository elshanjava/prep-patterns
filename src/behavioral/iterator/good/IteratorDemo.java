package behavioral.iterator.good;

import behavioral.iterator.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.StreamSupport;

public class IteratorDemo {
    public static void main(String[] args) {
        System.out.println("== Iterator [GOOD] ==");

        var batch = new TransactionBatch(List.of(
                new Transaction("tx-1", new BigDecimal("100")),
                new Transaction("tx-2", new BigDecimal("200")),
                new Transaction("tx-3", new BigDecimal("50")),
                new Transaction("tx-4", new BigDecimal("300")),
                new Transaction("tx-5", new BigDecimal("75"))
        ));

        // 1. for-each: реализация скрыта — не знаем, массив внутри или List или cursor в БД
        System.out.println("-- 1. for-each (реализация скрыта) --");
        for (Transaction tx : batch) {
            System.out.println(tx.id() + ": " + tx.amount());
        }

        // 2. Stream-операции: Iterable → Stream через spliterator
        System.out.println("-- 2. stream: сумма всех транзакций --");
        BigDecimal total = StreamSupport.stream(batch.spliterator(), false)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total: " + total);

        // 3. Фильтрация через stream: транзакции выше 100
        System.out.println("-- 3. filter+sum: только транзакции > 100 --");
        BigDecimal largeSum = StreamSupport.stream(batch.spliterator(), false)
                .filter(tx -> tx.amount().compareTo(new BigDecimal("100")) > 0)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("large tx sum: " + largeSum);

        // 4. PagedIterator: подгружает из «БД» страницами по 2 элемента
        // Клиентский код одинаков для всех вариантов: forEachRemaining или цикл while
        System.out.println("-- 4. PagedIterator (2 страницы по 2 элемента) --");
        List<List<Transaction>> pages = List.of(
                List.of(new Transaction("p1-tx1", new BigDecimal("500")),
                        new Transaction("p1-tx2", new BigDecimal("700"))),
                List.of(new Transaction("p2-tx1", new BigDecimal("99")))
        );
        var paged = new PagedIterator<Transaction>(
                i -> i < pages.size() ? pages.get(i) : List.of(), 2);
        paged.forEachRemaining(tx -> System.out.println(tx.id() + ": " + tx.amount()));

        // Если завтра TransactionBatch поменяет внутреннее хранение с List на TreeMap
        // или на cursor БД — ни один из 4 блоков выше менять не нужно.
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - смена List на cursor БД → клиентский код не меняется ни в одном месте");
        System.out.println("  - Iterable → бесплатная совместимость с for-each и Stream API");
        System.out.println("  - PagedIterator: ленивая подгрузка прозрачна для вызывающего кода");
        System.out.println("  - изолированный тест: new TransactionBatch(List.of(...)) — без зависимостей");
    }
}
