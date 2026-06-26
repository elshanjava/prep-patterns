package coding.behavioral.interpreter;

public record Or(Expr left, Expr right) implements Expr {

    @Override
    public boolean evaluate(Tx ctx) {
        return left.evaluate(ctx) || right.evaluate(ctx);
    }
}
