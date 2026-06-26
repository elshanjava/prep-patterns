package coding.behavioral.interpreter;

import java.math.BigDecimal;

public record AmountAbove(BigDecimal threshold) implements Expr {

    @Override
    public boolean evaluate(Tx ctx) {
        return ctx.amount().compareTo(threshold) > 0;
    }
}
