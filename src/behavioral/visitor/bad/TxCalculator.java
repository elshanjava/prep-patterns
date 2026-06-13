package behavioral.visitor.bad;

import java.math.BigDecimal;

// instanceof-каскад вместо double dispatch.
// Забыл тип → молчаливый баг; добавить тип → правка каждой такой функции.
final class TxCalculator {
    BigDecimal amount(Object node) {
        if (node instanceof Transfer t) return t.sum();
        if (node instanceof Fee f)      return f.value();
        if (node instanceof Refund r)   return r.sum().negate();
        throw new IllegalArgumentException("unknown node: " + node.getClass().getSimpleName());
    }
}
