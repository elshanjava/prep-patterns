package behavioral.state;

final class Pending implements PaymentState {
    public PaymentState capture() { return new Authorized(); }
    public PaymentState refund()  { return illegal("refund"); }
}
