package behavioral.interpreter;

record Not(Rule inner) implements Rule {
    public boolean interpret(Tx tx) {
        return !inner.interpret(tx);
    }
}
