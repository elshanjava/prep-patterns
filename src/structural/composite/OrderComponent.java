package structural.composite;

import java.math.BigDecimal;

interface OrderComponent {
    BigDecimal total();                          // одна операция для всех
}
