package structural.adapter;

import structural.adapter.model.Money;

import java.util.Currency;

// Клиент паттерна. Зависит ТОЛЬКО от целевого интерфейса CurrencyConverter,
// про существование LegacyFxSdk не знает — его прячет адаптер.
class PricingService {
    private final CurrencyConverter converter;
    PricingService(CurrencyConverter converter) { this.converter = converter; }

    Money quote(Money amount, Currency target) {
        return converter.convert(amount, target);
    }
}
