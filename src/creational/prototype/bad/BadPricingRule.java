package creational.prototype.bad;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Поверхностная копия: хранит ссылку на ту же карту.
// Изменение в "копии" незаметно меняет оригинал — скрытый баг.
final class BadPricingRule {
    Map<String, BigDecimal> tiers;  // публичное мутабельное поле

    BadPricingRule(Map<String, BigDecimal> tiers) {
        this.tiers = tiers;         // просто сохраняем ссылку — НЕ копируем
    }

    // "Копия" разделяет ту же карту с оригиналом
    BadPricingRule shallowCopy() {
        return new BadPricingRule(this.tiers); // та же map!
    }

    // Попытка сделать "модификацию без изменения оригинала" — но на деле меняет оба
    BadPricingRule withTier(String key, BigDecimal value) {
        this.tiers.put(key, value); // мутирует исходную карту!
        return this;
    }

    @Override public String toString() { return "BadPricingRule" + tiers; }
}
