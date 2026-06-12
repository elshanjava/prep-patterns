package creational.prototype;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class PricingRule {
    private final Map<String, BigDecimal> tiers;   // иммутабельное поле-ссылка
 
    public PricingRule(Map<String, BigDecimal> tiers) {
        this.tiers = Map.copyOf(tiers);            // защитная копия на входе
    }
 
    // copy-constructor = "прототип": честная глубокая копия, вызывает конструктор
    public PricingRule(PricingRule other) {
        this.tiers = Map.copyOf(other.tiers);      // НОВАЯ мапа, состояние не шарится
    }
 
    // вариант "изменённой копии" без мутации оригинала
    public PricingRule withTier(String key, BigDecimal value) {
        var copy = new HashMap<>(this.tiers);
        copy.put(key, value);
        return new PricingRule(copy);              // оригинал не тронут
    }

    public BigDecimal tier(String key) { return tiers.get(key); }

    @Override public String toString() { return "PricingRule" + tiers; }
}
