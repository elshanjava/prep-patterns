package coding.behavioral.visitor;

public interface OperationVisitor<R> {
    R visit(NumberNode numberNode);
    R visit(AddNode addNode);
    R visit(MulNode mulNode);
}
