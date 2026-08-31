package pattern.behavioral.interpreter.good;

import pattern.behavioral.interpreter.model.Tx;

record And(Rule left, Rule right) implements Rule {
    public boolean interpret(Tx tx) {
        return left.interpret(tx) && right.interpret(tx);
    }
}
