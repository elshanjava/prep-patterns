package behavioral.visitor.good;

import java.math.BigDecimal;

// Новая операция = новый visitor; классы узлов не трогаем.
final class AmountVisitor implements TxVisitor<BigDecimal> {
    public BigDecimal visit(Transfer t) { return t.sum; }
    public BigDecimal visit(Fee f)      { return f.value; }
    public BigDecimal visit(Refund r)   { return r.sum.negate(); }
}
