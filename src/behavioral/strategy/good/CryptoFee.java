package behavioral.strategy.good;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;

// Добавление новой стратегии = 1 новый файл.
// BadFeeCalculator и BadReceiptGenerator потребовали правки switch в двух местах.
// Здесь: FeeCalculator, CardFee, SepaFee — не трогаются.
// В Spring: достаточно добавить @Component — List<FeeStrategy> получит его автоматически.
final class CryptoFee implements FeeStrategy {
    private static final BigDecimal RATE = new BigDecimal("0.015");

    public PaymentMethod method() { return PaymentMethod.CRYPTO; }

    public BigDecimal fee(Payment p) {
        return p.amount().multiply(RATE);
    }
}
