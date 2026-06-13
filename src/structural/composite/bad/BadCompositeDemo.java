package structural.composite.bad;

import java.math.BigDecimal;
import java.util.List;

public class BadCompositeDemo {
    public static void main(String[] args) {
        System.out.println("== Composite [BAD] — instanceof everywhere ==");

        var calc = new BadOrderCalculator();

        var order = List.of(
                new BadLineItem(new BigDecimal("9.99")),
                new BadLineItem(new BigDecimal("4.50")),
                List.of(                               // вложенный "бандл"
                        new BadLineItem(new BigDecimal("2.00")),
                        new BadLineItem(new BigDecimal("3.00"))
                )
        );

        System.out.println("total: " + calc.total(order));

        System.out.println();
        System.out.println("Добавить GiftCard как тип элемента:");
        System.out.println("  → добавить instanceof в total()");
        System.out.println("  → добавить instanceof в discount()");
        System.out.println("  → добавить instanceof в tax()");
        System.out.println("  → добавить instanceof в export() ...");
    }
}
