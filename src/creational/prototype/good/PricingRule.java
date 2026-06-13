package creational.prototype.good;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Глубокая копия: withTier() и deepCopy() возвращают НОВЫЙ объект с НОВОЙ картой.
// Оригинал остаётся неизменным — ни один вызов не мутирует чужое состояние.
final class PricingRule {
    private final Map<String, BigDecimal> tiers;

    private PricingRule(Map<String, BigDecimal> tiers) {
        // Защитная копия: внешний caller не может мутировать наш state через переданную map
        this.tiers = Collections.unmodifiableMap(new HashMap<>(tiers));
    }

    static PricingRule of(Map<String, BigDecimal> tiers) {
        return new PricingRule(tiers);
    }

    // Создаёт новое правило с добавленным/изменённым тиром — оригинал не тронут
    PricingRule withTier(String key, BigDecimal value) {
        var copy = new HashMap<>(tiers);
        copy.put(key, value);
        return new PricingRule(copy);
    }

    // Полная копия для передачи в изолированный воркер
    PricingRule deepCopy() {
        return new PricingRule(tiers);
    }

    Map<String, BigDecimal> tiers() { return tiers; }

    @Override public String toString() { return "PricingRule" + tiers; }
}
