package behavioral.state.good;

final class Refunded implements PaymentState {
    public PaymentState capture() { return illegal("capture"); }
    public PaymentState refund()  { return illegal("refund"); }
}
