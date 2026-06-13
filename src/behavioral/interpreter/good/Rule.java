package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

interface Rule {
    boolean interpret(Tx tx);
}
