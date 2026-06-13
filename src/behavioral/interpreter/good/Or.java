package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

record Or(Rule left, Rule right) implements Rule {
    public boolean interpret(Tx tx) {
        return left.interpret(tx) || right.interpret(tx);
    }
}
