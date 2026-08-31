package pattern.behavioral.interpreter.good;

import pattern.behavioral.interpreter.model.Tx;

record Not(Rule inner) implements Rule {
    public boolean interpret(Tx tx) {
        return !inner.interpret(tx);
    }
}
