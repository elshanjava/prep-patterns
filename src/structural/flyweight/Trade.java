package structural.flyweight;

import java.math.BigDecimal;

record Trade(long id, Currency currency, BigDecimal amount) {}
// 10 млн сделок в USD ссылаются на ОДИН Currency.of("USD")
