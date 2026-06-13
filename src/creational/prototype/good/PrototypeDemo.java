package creational.prototype.good;

import java.math.BigDecimal;
import java.util.Map;

public class PrototypeDemo {
    public static void main(String[] args) {
        System.out.println("== Prototype [GOOD] — глубокая копия, оригинал всегда неизменён ==");

        var base = PricingRule.of(Map.of(
                "standard", new BigDecimal("0.029"),
                "premium",  new BigDecimal("0.019")
        ));
        System.out.println("base:    " + base);

        // Создаём VIP-правило на основе шаблона — base не меняется
        var vip = base.withTier("vip", new BigDecimal("0.009"));
        System.out.println("vip:     " + vip);
        System.out.println("base:    " + base);  // остался прежним

        System.out.println("base == vip?                   " + (base == vip));            // false
        System.out.println("base содержит 'vip'?           " + base.tiers().containsKey("vip")); // false

        // Параллельная обработка: каждый воркер получает независимую копию
        var copy = base.deepCopy();
        System.out.println("copy == base?                  " + (copy == base));            // false
        System.out.println("copy данные == base данные?    " + copy.tiers().equals(base.tiers())); // true

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - withTier() не мутирует оригинал — нет скрытых side effects");
        System.out.println("  - tiers() возвращает unmodifiableMap — клиент не сломает состояние");
        System.out.println("  - deepCopy() явно выражает намерение: «создай независимый клон»");
    }
}
