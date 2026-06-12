package creational.prototype;

import java.math.BigDecimal;
import java.util.Map;

// Запуск: копирование вместо повторной дорогой сборки; копия не делит состояние.
public class PrototypeDemo {
    public static void main(String[] args) {
        System.out.println("== Prototype ==");

        PricingRule base = new PricingRule(Map.of("default", new BigDecimal("0.029")));
        PricingRule premium = base.withTier("premium", new BigDecimal("0.019"));
        PricingRule copy = new PricingRule(base);   // copy-constructor

        System.out.println("base:    " + base);
        System.out.println("premium: " + premium + "  (base не тронут)");
        System.out.println("copy:    " + copy);
        System.out.println("copy — отдельный объект: " + (copy != base));
    }
}
