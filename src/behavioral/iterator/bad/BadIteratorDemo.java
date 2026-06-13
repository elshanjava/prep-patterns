package behavioral.iterator.bad;

import behavioral.iterator.model.Transaction;

import java.math.BigDecimal;

public class BadIteratorDemo {
    public static void main(String[] args) {
        System.out.println("== Iterator [BAD] ==");

        var batch = new BadTransactionBatch(
                new Transaction("tx-1", new BigDecimal("100")),
                new Transaction("tx-2", new BigDecimal("200"))
        );

        // клиент знает, что внутри массив — смена структуры ломает этот код
        for (int i = 0; i < batch.internalArray().length; i++) {
            Transaction tx = batch.internalArray()[i];
            System.out.println(tx.id() + ": " + tx.amount());
        }
    }
}
