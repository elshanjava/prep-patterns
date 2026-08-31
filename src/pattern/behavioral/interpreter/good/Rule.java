package pattern.behavioral.interpreter.good;

import pattern.behavioral.interpreter.model.Tx;

interface Rule {
    boolean interpret(Tx tx);
}
