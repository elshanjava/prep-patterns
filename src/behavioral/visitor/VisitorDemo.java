package behavioral.visitor;

import java.math.BigDecimal;
import java.util.List;

public class VisitorDemo {
    public static void main(String[] args) {
        System.out.println("== Visitor ==");

        List<TxNode> nodes = List.of(
                new Transfer(new BigDecimal("1000")),
                new Fee(new BigDecimal("29")),
                new Refund(new BigDecimal("500"))
        );

        var visitor = new AmountVisitor();

        System.out.println("amounts: " + nodes.stream()
                .map(n -> n.accept(visitor).toPlainString())
                .toList());

        BigDecimal total = nodes.stream()
                .map(n -> n.accept(visitor))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total:   " + total);
    }
}
