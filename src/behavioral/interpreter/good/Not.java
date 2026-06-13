package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

record Not(Rule inner) implements Rule {
    public boolean interpret(Tx tx) {
        return !inner.interpret(tx);
    }
}
