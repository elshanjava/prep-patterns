package behavioral.state.good;

interface PaymentState {
    PaymentState capture();
    PaymentState refund();
    PaymentState cancel();

    default PaymentState illegal(String op) {
        throw new IllegalStateException(op + " недопустим в " + getClass().getSimpleName());
    }
}
