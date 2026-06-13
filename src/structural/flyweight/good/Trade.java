package structural.flyweight.good;

import java.math.BigDecimal;

// Сделка хранит ссылку на разделяемый объект Currency — не отдельную копию.
record Trade(long id, Currency currency, BigDecimal amount) {}
