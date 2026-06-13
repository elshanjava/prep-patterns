package behavioral.visitor.good;

import java.math.BigDecimal;
import java.util.List;

public class VisitorDemo {
    public static void main(String[] args) {
        System.out.println("== Visitor [GOOD] ==");

        // Settlement добавлен как новый тип — узлы не менялись
        List<TxNode> nodes = List.of(
                new Transfer(new BigDecimal("1000")),
                new Fee(new BigDecimal("29")),
                new Refund(new BigDecimal("500")),
                new Settlement(new BigDecimal("350"), "Raiffeisen")   // новый тип
        );

        // 1. AmountVisitor: суммы по всем узлам
        System.out.println("-- 1. AmountVisitor --");
        var amountVisitor = new AmountVisitor();
        System.out.println("amounts: " + nodes.stream()
                .map(n -> n.accept(amountVisitor).toPlainString())
                .toList());
        BigDecimal total = nodes.stream()
                .map(n -> n.accept(amountVisitor))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total:   " + total);

        // 2. TaxVisitor: та же коллекция узлов, другой посетитель — узлы не меняются
        System.out.println();
        System.out.println("-- 2. TaxVisitor (НДС 20%) --");
        var taxVisitor = new TaxVisitor();
        System.out.println("taxes:   " + nodes.stream()
                .map(n -> n.accept(taxVisitor).toPlainString())
                .toList());
        BigDecimal totalTax = nodes.stream()
                .map(n -> n.accept(taxVisitor))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total tax: " + totalTax);

        // 3. Settlement добавлен: только реализовали accept() в Settlement.java
        //    и добавили visit(Settlement) в TxVisitor + оба Visitor.
        //    Компилятор указал все места → нельзя забыть.
        //    В bad/: нужно было вставить instanceof вручную в amount() и taxAmount() — без помощи компилятора.
        System.out.println();
        System.out.println("-- 3. Settlement в обоих Visitor --");
        var settlement = new Settlement(new BigDecimal("200"), "Sberbank");
        System.out.println("amount: " + settlement.accept(amountVisitor));
        System.out.println("tax:    " + settlement.accept(taxVisitor));

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - добавить AuditVisitor = 1 файл, узлы не трогаются");
        System.out.println("  - добавить Settlement тип: компилятор указывает все Visitor для обновления");
        System.out.println("  - 2 операции × N типов: 2 visitor-класса, не 2×N instanceof-веток");
        System.out.println("  - тест AmountVisitor изолированно: new AmountVisitor().visit(new Fee(...))");
    }
}
