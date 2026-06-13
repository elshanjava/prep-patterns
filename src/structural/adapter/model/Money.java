package structural.adapter.model;

import java.math.BigDecimal;
import java.util.Currency;

public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money ofCents(BigDecimal cents, Currency currency) {
        return new Money(cents.movePointLeft(2), currency);
    }

    public Currency currency() { return currency; }
    public double toCents()    { return amount.movePointRight(2).doubleValue(); }

    @Override public String toString() { return amount + " " + currency.getCurrencyCode(); }
}
