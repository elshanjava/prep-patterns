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

        // Метод 1: суммы
        System.out.println("-- суммы (amount) --");
        BigDecimal total = nodes.stream()
                .map(calc::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total amount: " + total);

        // Метод 2: налоги — тот же instanceof-каскад продублирован в taxAmount()
        System.out.println("-- налоги (taxAmount) --");
        BigDecimal totalTax = nodes.stream()
                .map(calc::taxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total tax: " + totalTax);

        System.out.println();
        // Добавить тип Settlement:
        //   → нужно добавить instanceof-ветку в amount() И в taxAmount()
        //   → забыть одну → RuntimeException только в prod при первом Settlement-узле
        // Добавить метод auditAmount():
        //   → скопировать instanceof-каскад в 3-й раз
        System.out.println("Проблемы:");
        System.out.println("  - добавить Settlement тип = instanceof в amount() + в taxAmount() = 2 правки");
        System.out.println("  - добавить auditAmount() = 3-й instanceof-каскад (copy-paste)");
        System.out.println("  - забыть Settlement в taxAmount() = RuntimeException только в prod");
        System.out.println("  - 2 метода × 3 типа = 6 instanceof-веток: читаемость падает с N×M");
    }
}
