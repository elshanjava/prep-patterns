package behavioral.state;

final class Authorized implements PaymentState {
    public PaymentState capture() { return illegal("capture"); }
    public PaymentState refund()  { return new Refunded(); }
}
