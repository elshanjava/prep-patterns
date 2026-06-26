package coding.behavioral.interpreter;

public record Not(Expr expr) implements Expr {

    @Override
    public boolean evaluate(Tx ctx) {
        return !expr.evaluate(ctx);
    }
}
