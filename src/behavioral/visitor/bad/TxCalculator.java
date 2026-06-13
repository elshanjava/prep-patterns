package behavioral.visitor.bad;

import java.math.BigDecimal;

// instanceof-каскад вместо double dispatch.
// Забыл тип → молчаливый баг (или RuntimeException только в prod).
// Добавить новую операцию taxAmount → скопировать тот же instanceof-каскад.
// Добавить новый тип Settlement → нужно вставить instanceof-ветку в КАЖДЫЙ метод.
final class TxCalculator {

    // Метод 1: сумма транзакции
    BigDecimal amount(Object node) {
        if (node instanceof Transfer t) return t.sum();
        if (node instanceof Fee f)      return f.value();
        if (node instanceof Refund r)   return r.sum().negate();
        // Settlement здесь не обрабатывается: добавить нужно и сюда, и в taxAmount()
        throw new IllegalArgumentException("unknown node: " + node.getClass().getSimpleName());
    }

    // Метод 2: налог — тот же instanceof-каскад продублирован
    // 2 метода × N типов = 2×N instanceof-веток
    // Добавить Settlement → исправить amount() + taxAmount()
    // Добавить AuditAmount() → скопировать instanceof-каскад в 3-й раз
    BigDecimal taxAmount(Object node) {
        if (node instanceof Transfer t) return t.sum().multiply(new BigDecimal("0.20"));   // НДС 20%
        if (node instanceof Fee f)      return f.value().multiply(new BigDecimal("0.20"));  // НДС на комиссию
        if (node instanceof Refund r)   return r.sum().negate().multiply(new BigDecimal("0.20")); // возврат НДС
        // Settlement: забыли добавить — throw появится только в рантайме
        throw new IllegalArgumentException("unknown node for tax: " + node.getClass().getSimpleName());
    }
}
