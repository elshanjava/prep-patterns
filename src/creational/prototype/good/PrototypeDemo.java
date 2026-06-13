package creational.prototype.good;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PrototypeDemo {
    public static void main(String[] args) {
        System.out.println("== Prototype [GOOD] — глубокая копия, оригинал всегда неизменён ==");
        System.out.println();

        var base = PricingRule.of(
                Map.of("standard", new BigDecimal("0.029"),
                       "premium",  new BigDecimal("0.019")),
                List.of("KP", "IR")
        );
        System.out.println("base: " + base);

        // Воркер-1 добавляет vip-тир для своего клиента
        var worker1 = base.withTier("vip", new BigDecimal("0.009"));
        // Воркер-2 блокирует страну
        var worker2 = base.blockCountry("SY");

        System.out.println();
        System.out.println("worker1 (vip): " + worker1);
        System.out.println("worker2 (+SY): " + worker2);
        System.out.println("base ПОСЛЕ:    " + base);  // ← оригинал не изменился

        System.out.println();
        System.out.println("независимость коллекций:");
        System.out.println("  base.tiers == worker1.tiers? "
                           + (base.tiers() == worker1.tiers()));            // false
        System.out.println("  base.blocked == worker2.blocked? "
                           + (base.blockedCountries() == worker2.blockedCountries())); // false

        System.out.println();
        System.out.println("--- Параллельные воркеры с независимыми копиями ---");
        var w1 = base.deepCopy().withTier("enterprise", new BigDecimal("0.005"));
        var w2 = base.deepCopy().blockCountry("RU");
        System.out.println("w1: " + w1);
        System.out.println("w2: " + w2);
        System.out.println("base: " + base);  // всё ещё неизменён

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  withTier() возвращает НОВЫЙ объект — оригинал никогда не меняется");
        System.out.println("  blockCountry() то же — можно безопасно передавать параллельным потокам");
        System.out.println("  Collections.unmodifiableMap: клиент не сможет напрямую мутировать tiers");
        System.out.println("  deepCopy() явно выражает намерение «создай независимый клон для воркера»");
    }
}
