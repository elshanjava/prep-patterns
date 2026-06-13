package structural.flyweight.bad;

import java.math.BigDecimal;

// Каждая сделка создаёт свой объект валюты — даже если их миллион в USD.
record BadTrade(long id, BadCurrency currency, BigDecimal amount) {}
