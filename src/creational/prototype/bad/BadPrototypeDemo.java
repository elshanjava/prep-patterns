package creational.prototype.bad;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BadPrototypeDemo {
    public static void main(String[] args) {
        System.out.println("== Prototype [BAD] — shared mutable state ==");

        var base = new BadPricingRule(new HashMap<>(Map.of("default", new BigDecimal("0.029"))));
        System.out.println("base before copy:    " + base);

        var copy = base.shallowCopy();

        // "копия" меняет тот же тир — база испорчена!
        copy.tiers.put("default", new BigDecimal("0.999"));

        System.out.println("copy after mutation: " + copy);
        System.out.println("base after mutation: " + base);   // ← тоже изменился!
        System.out.println();

        // Аналогично с withTier: вместо нового объекта мутирует исходный
        var base2  = new BadPricingRule(new HashMap<>(Map.of("base", new BigDecimal("0.029"))));
        var result = base2.withTier("premium", new BigDecimal("0.019")); // должен вернуть новый
        System.out.println("same object? " + (result == base2));         // true — оригинал изменён
        System.out.println("base2 after withTier: " + base2);
    }
}
