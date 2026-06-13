package behavioral.iterator;

import java.math.BigDecimal;
import java.util.List;

public class IteratorDemo {
    public static void main(String[] args) {
        System.out.println("== Iterator ==");

        var batch = new TransactionBatch(List.of(
                new Transaction("tx-1", new BigDecimal("100")),
                new Transaction("tx-2", new BigDecimal("200")),
                new Transaction("tx-3", new BigDecimal("300"))
        ));

        System.out.println("-- for-each (реализация скрыта) --");
        for (Transaction tx : batch) {
            System.out.println(tx.id() + ": " + tx.amount());
        }

        System.out.println("-- paged iterator (page size 2) --");
        List<List<Transaction>> pages = List.of(
                List.of(new Transaction("p1-tx1", BigDecimal.ONE),
                        new Transaction("p1-tx2", BigDecimal.TEN)),
                List.of(new Transaction("p2-tx1", new BigDecimal("99")))
        );
        var paged = new PagedIterator<Transaction>(
                i -> i < pages.size() ? pages.get(i) : List.of(), 2);
        paged.forEachRemaining(tx -> System.out.println(tx.id() + ": " + tx.amount()));
    }
}
