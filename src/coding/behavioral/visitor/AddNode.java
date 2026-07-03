package coding.behavioral.visitor;

// Ветка: сумма двух поддеревьев.
public class AddNode implements OperationNode {
    private final OperationNode left;
    private final OperationNode right;

    public AddNode(OperationNode left, OperationNode right) {
        this.left = left;
        this.right = right;
    }

    public OperationNode left() {
        return left;
    }

    public OperationNode right() {
        return right;
    }

    @Override
    public <R> R accept(OperationVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
