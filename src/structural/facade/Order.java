package structural.facade;

import java.util.Currency;

// Входная заявка на оплату. Раньше класса не было — фасад на него ссылался.
public record Order(String customer, Currency currency, long amountCents) {}
