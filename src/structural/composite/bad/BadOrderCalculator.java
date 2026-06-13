package structural.composite.bad;

import java.math.BigDecimal;

// Три метода — в каждом одна и та же instanceof-рекурсия.
// Добавить BadGiftCard:
//   → total():    +1 instanceof
//   → discount(): +1 instanceof
//   → tax():      +1 instanceof
// Пропустить один → неверный расчёт, тесты могут не поймать если GiftCard в бандле.
final class BadOrderCalculator {

    BigDecimal total(Object item) {
        if (item instanceof BadLineItem li) {
            return li.price();
        } else if (item instanceof BadBundle b) {
            return b.items().stream().map(this::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (item instanceof BadGiftCard gc) {
            return gc.value();
        } else {
            throw new IllegalArgumentException("Unknown item type: " + item.getClass().getSimpleName());
        }
    }

    // Та же рекурсия что в total() — скопипащена с правками
    BigDecimal discount(Object item) {
        if (item instanceof BadLineItem li) {
            return li.price().multiply(new BigDecimal("0.05")); // 5% скидка
        } else if (item instanceof BadBundle b) {
            return b.items().stream().map(this::discount).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (item instanceof BadGiftCard) {
            return BigDecimal.ZERO;                             // подарочные карты без скидки
        } else {
            throw new IllegalArgumentException("Unknown item type: " + item.getClass().getSimpleName());
        }
    }

    // Третья копия — если добавить BadPromoItem, правь здесь, в total() и в discount()
    BigDecimal tax(Object item) {
        if (item instanceof BadLineItem li) {
            return li.price().multiply(new BigDecimal("0.20")); // НДС 20%
        } else if (item instanceof BadBundle b) {
            return b.items().stream().map(this::tax).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (item instanceof BadGiftCard) {
            return BigDecimal.ZERO;                             // подарочные карты не облагаются
        } else {
            throw new IllegalArgumentException("Unknown item type: " + item.getClass().getSimpleName());
        }
    }
}
