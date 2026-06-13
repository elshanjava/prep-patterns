package behavioral.state.good;

// Добавление Captured = 1 новый файл.
// BadPayment потребовал правки 3 switch'ей в capture() + refund() + cancel().
// Здесь: никакой другой класс не менялся.
final class Captured implements PaymentState {
    public PaymentState capture() { return illegal("capture"); }  // уже captured
    public PaymentState refund()  { return new Refunded(); }      // возврат после списания
    public PaymentState cancel()  { return illegal("cancel"); }   // слишком поздно — использовать refund
}
