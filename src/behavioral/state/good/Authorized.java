package behavioral.state.good;

// Authorized знает только о своих переходах: capture → Captured, cancel → Refunded.
final class Authorized implements PaymentState {
    public PaymentState capture() { return new Captured(); }   // переход в Captured
    public PaymentState refund()  { return illegal("refund"); } // нельзя refund без capture
    public PaymentState cancel()  { return new Refunded(); }   // отмена до списания
}
