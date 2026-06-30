package coding.behavioral.strategy;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        // вес 3 кг, сумма заказа 120
        Order order = new Order(new BigDecimal("3"), new BigDecimal("120"));

        // --- один заказ, три стратегии ---
        System.out.println("-- один заказ тремя стратегиями --");
        ShippingService service = new ShippingService(new FlatRateShipping(new BigDecimal("10")));
        System.out.println("flat (фикс 10):              " + service.calculate(order));

        service.setShippingStrategy(new WeightShipping(new BigDecimal("2.5")));
        System.out.println("weight (2.5/кг * 3):         " + service.calculate(order));

        service.setShippingStrategy(new FreeOverShipping(new BigDecimal("100"), new BigDecimal("15")));
        System.out.println("freeOver (>100 бесплатно):   " + service.calculate(order));

        // --- тот же FreeOver, но заказ дешевле порога ---
        System.out.println();
        System.out.println("-- FreeOver на дешёвом заказе (total=80 < 100) --");
        Order cheap = new Order(new BigDecimal("3"), new BigDecimal("80"));
        System.out.println("freeOver:                    " + service.calculate(cheap));

        // --- лямбда как стратегия (ShippingStrategy функциональный) ---
        System.out.println();
        System.out.println("-- лямбда-стратегия (вес * 5) --");
        service.setShippingStrategy(o -> o.weight().multiply(new BigDecimal("5")));
        System.out.println("lambda:                      " + service.calculate(order));
    }
}
