package behavioral.visitor.good;

import java.math.BigDecimal;

final class Refund implements TxNode {
    final BigDecimal sum;

    Refund(BigDecimal sum) { this.sum = sum; }

    public <R> R accept(TxVisitor<R> v) { return v.visit(this); }
}
