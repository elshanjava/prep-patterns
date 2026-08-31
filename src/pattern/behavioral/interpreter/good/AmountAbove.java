package pattern.behavioral.interpreter.good;

import pattern.behavioral.interpreter.model.Tx;

import java.math.BigDecimal;

record AmountAbove(BigDecimal limit) implements Rule {
    public boolean interpret(Tx tx) {
        return tx.amount().compareTo(limit) > 0;
    }
}
