package behavioral.state.good;

// Контекст делегирует текущему состоянию; незаконные переходы невозможны «по забывчивости».
// Добавить DISPUTED = 1 новый класс Disputed implements PaymentState.
final class Payment {
    private PaymentState state = new Pending();

    public void capture() { state = state.capture(); }
    public void refund()  { state = state.refund(); }
    public void cancel()  { state = state.cancel(); }

    public String status() { return state.getClass().getSimpleName(); }
}
