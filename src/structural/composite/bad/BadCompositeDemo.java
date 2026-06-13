package structural.composite.bad;

import java.math.BigDecimal;
import java.util.List;

public class BadCompositeDemo {
    public static void main(String[] args) {
        System.out.println("== Composite [BAD] — 3 метода × 3 типа = 9 instanceof-веток ==");
        System.out.println();

        var calc = new BadOrderCalculator();

        var order = new BadBundle("Order", List.of(
                new BadLineItem(new BigDecimal("9.99")),
                new BadLineItem(new BigDecimal("4.50")),
                new BadGiftCard(new BigDecimal("5.00")),
                new BadBundle("starter pack", List.of(
                        new BadLineItem(new BigDecimal("2.00")),
                        new BadLineItem(new BigDecimal("3.00"))
                ))
        ));

        System.out.printf("total:    %s%n", calc.total(order));
        System.out.printf("discount: %s%n", calc.discount(order));
        System.out.printf("tax:      %s%n", calc.tax(order));

        System.out.println();
        System.out.println("Сейчас 3 типа × 3 метода = 9 instanceof-веток.");
        System.out.println("Добавить BadPromoItem:");
        System.out.println("  → total():    +1 instanceof ветка");
        System.out.println("  → discount(): +1 instanceof ветка");
        System.out.println("  → tax():      +1 instanceof ветка");
        System.out.println("  Пропустить одну ветку → exception или тихо неверный расчёт.");
        System.out.println("  export(), serialize(), print() — там тоже будет instanceof.");
    }
}
