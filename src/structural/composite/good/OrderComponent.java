package structural.composite.good;

import java.math.BigDecimal;

// Единый интерфейс для листа (LineItem) и контейнера (Bundle).
// Клиент вызывает total() — не зная, это один товар или вложенное дерево.
interface OrderComponent {
    String     name();
    BigDecimal total();
}
