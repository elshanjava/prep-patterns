package behavioral.state;

interface PaymentState {
    PaymentState capture();
    PaymentState refund();
    default PaymentState illegal(String op) {
        throw new IllegalStateException(op + " недопустим в " + getClass().getSimpleName());
    }
}
