package structural.adapter.model;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Доменные деньги: сумма + валюта, без double в арифметике.
 * Хранит сумму в основных единицах (BigDecimal), наружу умеет отдавать центы —
 * именно через этот тип адаптер общается с «грязным» легаси-SDK.
 */
public final class Money {
    private final BigDecimal amount;     // в основных единицах, напр. 12.34
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    /** Собрать из минорных единиц (центов) — обратная операция к toCents(). */
    public static Money ofCents(BigDecimal cents, Currency currency) {
        return new Money(cents.movePointLeft(2), currency);
    }

    public Currency currency() { return currency; }

    /** Сумма в минорных единицах (центах). */
    public double toCents() { return amount.movePointRight(2).doubleValue(); }

    @Override public String toString() { return amount + " " + currency.getCurrencyCode(); }
}
