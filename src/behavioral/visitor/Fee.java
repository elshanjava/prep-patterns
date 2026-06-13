package behavioral.visitor;

import java.math.BigDecimal;

final class Fee implements TxNode {
    final BigDecimal value;

    Fee(BigDecimal value) { this.value = value; }

    public <R> R accept(TxVisitor<R> v) { return v.visit(this); }
}
