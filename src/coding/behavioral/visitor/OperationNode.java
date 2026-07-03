package coding.behavioral.visitor;

public interface OperationNode {
    <R> R accept(OperationVisitor<R> visitor);
}
