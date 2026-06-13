package structural.composite.good;

import java.math.BigDecimal;
import java.util.List;

public class CompositeDemo {
    public static void main(String[] args) {
        System.out.println("== Composite [GOOD] — единый интерфейс для листа и дерева ==");

        var widget    = new LineItem("Widget",       new BigDecimal("10.00"));
        var gadget    = new LineItem("Gadget",        new BigDecimal("25.00"));
        var accessory = new LineItem("Accessory",     new BigDecimal("5.00"));
        var addon     = new LineItem("Premium addon", new BigDecimal("15.00"));

        // Бандл содержит бандл — рекурсия прозрачна для клиента
        var starterPack = new Bundle("Starter Pack", List.of(widget, gadget));
        var proPack     = new Bundle("Pro Bundle",   List.of(starterPack, accessory, addon));

        // Клиент вызывает total() одинаково для листа и для дерева
        printComponent(widget);
        printComponent(starterPack);
        printComponent(proPack);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - нет instanceof: полиморфизм через OrderComponent");
        System.out.println("  - добавить GiftCard: новый класс implements OrderComponent — всё работает");
        System.out.println("  - рекурсия живёт в Bundle, клиент её не видит и не дублирует");
        System.out.println("  - discount(), tax(), export() — реализуй в том же месте, без рассыпных if");
    }

    private static void printComponent(OrderComponent c) {
        System.out.printf("%-14s total: %s%n", c.name(), c.total());
    }
}
