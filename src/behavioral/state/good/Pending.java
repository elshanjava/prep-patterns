package behavioral.state.good;

// Pending знает только о своих допустимых переходах.
// Ничего не знает о Captured, Refunded и т.д.
final class Pending implements PaymentState {
    public PaymentState capture() { return new Authorized(); }
    public PaymentState refund()  { return illegal("refund"); }
    public PaymentState cancel()  { return new Refunded(); }  // отмена до авторизации
}
