package structural.composite.good;

import java.math.BigDecimal;
import java.util.List;

public class CompositeDemo {
    public static void main(String[] args) {
        System.out.println("== Composite [GOOD] — единый интерфейс, ноль instanceof ==");
        System.out.println();

        var widget    = new LineItem("Widget",       new BigDecimal("9.99"));
        var gadget    = new LineItem("Gadget",        new BigDecimal("4.50"));
        var giftCard  = new GiftCard("Gift Card",    new BigDecimal("5.00"));
        var innerItem = new LineItem("Inner Widget",  new BigDecimal("2.00"));
        var innerItem2= new LineItem("Inner Gadget",  new BigDecimal("3.00"));

        var starterPack = new Bundle("Starter Pack",  List.of(innerItem, innerItem2));
        var order       = new Bundle("Full Order",    List.of(widget, gadget, giftCard, starterPack));

        printComponent(widget);
        printComponent(giftCard);     // карта: discount=0, tax=0 — всё в контракте
        printComponent(starterPack);
        printComponent(order);

        System.out.println();
        System.out.println("Клиент вызывает одни и те же 3 метода — не зная о типах внутри.");
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  GiftCard добавлен: 1 файл (GiftCard.java), 0 изменений в Bundle");
        System.out.println("  Нет 9 instanceof-веток: все 3 метода — полиморфизм через контракт");
        System.out.println("  PromoItem, SubscriptionItem — тот же паттерн: 1 класс, всё работает");
        System.out.println("  Компилятор гарантирует: каждый новый тип реализует total+discount+tax");
    }

    private static void printComponent(OrderComponent c) {
        System.out.printf("%-16s total=%6s  discount=%5s  tax=%5s%n",
                c.name(), c.total(), c.discount(), c.tax());
    }
}
