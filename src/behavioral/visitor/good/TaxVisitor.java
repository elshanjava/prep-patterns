package behavioral.visitor.good;

import java.math.BigDecimal;

// Новая операция (налог) = новый visitor. TxNode-классы не меняются.
// В bad/ пришлось бы добавить taxAmount() в TxCalculator — ещё один instanceof-каскад.
final class TaxVisitor implements TxVisitor<BigDecimal> {
    private static final BigDecimal VAT = new BigDecimal("0.20"); // НДС 20%

    public BigDecimal visit(Transfer t)   { return t.sum.multiply(VAT); }
    public BigDecimal visit(Fee f)        { return f.value.multiply(VAT); }
    public BigDecimal visit(Refund r)     { return r.sum.negate().multiply(VAT); }  // возврат НДС
    public BigDecimal visit(Settlement s) { return s.net.multiply(VAT); }
}
