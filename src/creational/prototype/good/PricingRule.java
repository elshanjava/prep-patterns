package creational.prototype.good;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Глубокая копия: withTier(), blockCountry(), deepCopy() — все возвращают НОВЫЙ объект.
// Оригинал никогда не мутирует. Безопасно передавать параллельным воркерам.
final class PricingRule {
    private final Map<String, BigDecimal> tiers;
    private final List<String>            blockedCountries;

    private PricingRule(Map<String, BigDecimal> tiers, List<String> blockedCountries) {
        this.tiers            = Collections.unmodifiableMap(new HashMap<>(tiers));
        this.blockedCountries = Collections.unmodifiableList(new ArrayList<>(blockedCountries));
    }

    static PricingRule of(Map<String, BigDecimal> tiers, List<String> blockedCountries) {
        return new PricingRule(tiers, blockedCountries);
    }

    // Новый объект с добавленным тиром — оригинал не тронут
    PricingRule withTier(String key, BigDecimal value) {
        var t = new HashMap<>(tiers);
        t.put(key, value);
        return new PricingRule(t, blockedCountries);
    }

    // Новый объект с добавленной страной
    PricingRule blockCountry(String country) {
        var c = new ArrayList<>(blockedCountries);
        c.add(country);
        return new PricingRule(tiers, c);
    }

    // Полная независимая копия для воркера
    PricingRule deepCopy() { return new PricingRule(tiers, blockedCountries); }

    boolean isBlocked(String country)        { return blockedCountries.contains(country); }
    BigDecimal tierRate(String tier)          { return tiers.getOrDefault(tier, tiers.get("standard")); }
    Map<String, BigDecimal> tiers()          { return tiers; }
    List<String> blockedCountries()          { return blockedCountries; }

    @Override public String toString() {
        return "PricingRule{tiers=" + tiers + ", blocked=" + blockedCountries + "}";
    }
}
