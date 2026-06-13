package structural.composite.bad;

import java.math.BigDecimal;

// Новый тип элемента заказа.
// Добавить его = найти и отредактировать КАЖДЫЙ метод в BadOrderCalculator.
// Пропустить хотя бы один → NullPointerException или неверный расчёт.
final class BadGiftCard {
    private final BigDecimal value;
    BadGiftCard(BigDecimal value) { this.value = value; }
    BigDecimal value() { return value; }
}
