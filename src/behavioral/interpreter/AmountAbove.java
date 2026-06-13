package behavioral.interpreter;

import java.math.BigDecimal;

record AmountAbove(BigDecimal limit) implements Rule {
    public boolean interpret(Tx tx) {
        return tx.amount().compareTo(limit) > 0;
    }
}
