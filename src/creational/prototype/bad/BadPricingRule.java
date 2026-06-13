package creational.prototype.bad;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Поверхностная копия: хранит ссылки на те же коллекции.
// Изменение в «копии» молча меняет оригинал — и наоборот.
// Два воркера получают «независимые» правила и ломают друг другу состояние.
final class BadPricingRule {
    Map<String, BigDecimal> tiers;             // публичное мутабельное поле
    List<String>            blockedCountries;  // второе публичное мутабельное поле

    BadPricingRule(Map<String, BigDecimal> tiers, List<String> blockedCountries) {
        this.tiers            = tiers;            // сохраняем ссылку — НЕ копируем
        this.blockedCountries = blockedCountries; // та же ссылка!
    }

    // «Копия» разделяет те же коллекции с оригиналом
    BadPricingRule shallowCopy() {
        return new BadPricingRule(this.tiers, this.blockedCountries);  // те же объекты!
    }

    // Попытка добавить тир «без изменения оригинала» — на деле меняет оба объекта
    BadPricingRule withTier(String key, BigDecimal value) {
        this.tiers.put(key, value);  // мутирует исходную карту — оригинал тоже изменится!
        return this;
    }

    // Попытка добавить страну — та же проблема
    void blockCountry(String country) {
        this.blockedCountries.add(country);  // мутирует список оригинала!
    }

    @Override public String toString() {
        return "BadPricingRule{tiers=" + tiers + ", blocked=" + blockedCountries + "}";
    }
}
