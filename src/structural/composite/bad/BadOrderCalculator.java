package structural.composite.bad;

import java.math.BigDecimal;
import java.util.List;

// instanceof-проверки вместо полиморфизма.
// Клиент обязан знать все конкретные типы; добавить GiftCard → правь каждый such-метод.
final class BadOrderCalculator {

    // Дублируется в каждом месте, где нужна рекурсия по дереву заказа
    BigDecimal total(Object item) {
        if (item instanceof BadLineItem li) {
            return li.price();

        } else if (item instanceof List<?> bundle) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Object child : bundle) {
                sum = sum.add(total(child));  // рекурсия с instanceof — хрупко
            }
            return sum;

        } else {
            throw new IllegalArgumentException("Unknown item: " + item.getClass());
            // добавить GiftCard? — найди ВСЕ such-методы (total, discount, tax, ...)
            // и добавь instanceof-ветку в каждый
        }
    }
}
