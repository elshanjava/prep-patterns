package behavioral.state.good;

// Контекст делегирует текущему состоянию; незаконные переходы невозможны «по забывчивости».
final class Payment {
    private PaymentState state = new Pending();

    public void capture() { state = state.capture(); }
    public void refund()  { state = state.refund(); }

    public String status() { return state.getClass().getSimpleName(); }
}
