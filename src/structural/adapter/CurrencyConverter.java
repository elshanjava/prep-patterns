package structural.adapter;

import structural.adapter.model.Money;

import java.util.Currency;

public interface CurrencyConverter {
    Money convert(Money amount, Currency target);
}
