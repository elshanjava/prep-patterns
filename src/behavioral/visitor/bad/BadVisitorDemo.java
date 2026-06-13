package behavioral.visitor.bad;

import java.math.BigDecimal;
import java.util.List;

public class BadVisitorDemo {
    public static void main(String[] args) {
        System.out.println("== Visitor [BAD] ==");

        var nodes = List.of(
                new Transfer(new BigDecimal("1000")),
                new Fee(new BigDecimal("29")),
                new Refund(new BigDecimal("500"))
        );

        var calc = new TxCalculator();
        // каждая новая операция (tax, export, audit) = новый instanceof-каскад
        BigDecimal total = nodes.stream()
                .map(calc::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total: " + total);
    }
}
