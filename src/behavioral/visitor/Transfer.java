package behavioral.visitor;

import java.math.BigDecimal;

final class Transfer implements TxNode {
    final BigDecimal sum;

    Transfer(BigDecimal sum) { this.sum = sum; }

    public <R> R accept(TxVisitor<R> v) { return v.visit(this); }
}
