package behavioral.interpreter;

record Or(Rule left, Rule right) implements Rule {
    public boolean interpret(Tx tx) {
        return left.interpret(tx) || right.interpret(tx);
    }
}
