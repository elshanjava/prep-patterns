package coding.behavioral.visitor;

import java.math.BigDecimal;

// Лист дерева: просто число.
public class NumberNode implements OperationNode {
    private final BigDecimal value;

    public NumberNode(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public <R> R accept(OperationVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
