package coding.behavioral.visitor;

import java.math.BigDecimal;

public class CalculateVisitor implements OperationVisitor<BigDecimal> {

    @Override
    public BigDecimal visit(NumberNode numberNode) {
        return numberNode.value();
    }

    @Override
    public BigDecimal visit(AddNode addNode) {
        return addNode.left().accept(this).add(addNode.right().accept(this));
    }

    @Override
    public BigDecimal visit(MulNode mulNode) {
        return mulNode.left().accept(this).multiply(mulNode.right().accept(this));
    }
}
