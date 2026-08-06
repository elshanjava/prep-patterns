package coding.behavioral.visitor;

public class PrintVisitor implements OperationVisitor<String> {


    @Override
    public String visit(NumberNode numberNode) {
        return  numberNode.value().toPlainString();
    }

    @Override
    public String visit(AddNode addNode) {
        return "(" + addNode.left().accept(this) + " + " + addNode.right().accept(this) + ")";
    }

    @Override
    public String visit(MulNode mulNode) {
        return "(" + mulNode.left().accept(this) + " * " + mulNode.right().accept(this) + ")";
    }
}
