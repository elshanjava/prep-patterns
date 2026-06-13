package structural.composite.good;

import java.math.BigDecimal;

// Добавить новый тип: 1 новый файл, 0 изменений в Bundle, BadOrderCalculator, CompositeDemo.
// Компилятор требует реализовать все 3 метода контракта — забыть невозможно.
record GiftCard(String name, BigDecimal value) implements OrderComponent {
    @Override public BigDecimal total()    { return value; }
    @Override public BigDecimal discount() { return BigDecimal.ZERO; }  // карты без скидки
    @Override public BigDecimal tax()      { return BigDecimal.ZERO; }  // и без НДС
}
