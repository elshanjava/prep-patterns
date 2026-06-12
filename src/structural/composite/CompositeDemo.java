package structural.composite;

import java.math.BigDecimal;

// Запуск: к листу (LineItem) и к ветке (Bundle) клиент обращается одинаково —
// через OrderComponent.total(). Рекурсия по дереву спрятана внутри Bundle.
public class CompositeDemo {
    public static void main(String[] args) {
        System.out.println("== Composite ==");

        Bundle order = new Bundle();
        order.add(new LineItem(new BigDecimal("9.99")));
        order.add(new LineItem(new BigDecimal("4.50")));

        Bundle promo = new Bundle();                 // вложенный композит
        promo.add(new LineItem(new BigDecimal("2.00")));
        promo.add(new LineItem(new BigDecimal("3.00")));
        order.add(promo);

        System.out.println("order total = " + order.total());   // 19.49
    }
}
