package structural.facade.model;

import java.util.Currency;

public record Order(String customer, Currency currency, long amountCents) {}
