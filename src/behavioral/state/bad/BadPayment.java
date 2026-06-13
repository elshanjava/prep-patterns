package behavioral.state.bad;

// Логика переходов размазана по switch-блокам в каждом методе.
// Добавить статус = править все switch'и; легко пропустить кейс.
final class BadPayment {
    private PaymentStatus status = PaymentStatus.PENDING;

    public void capture() {
        switch (status) {
            case PENDING    -> status = PaymentStatus.AUTHORIZED;
            case AUTHORIZED -> throw new IllegalStateException("already authorized");
            case REFUNDED   -> throw new IllegalStateException("already refunded");
            // забыл новый статус → молчаливый баг
        }
    }

    public void refund() {
        switch (status) {
            case AUTHORIZED -> status = PaymentStatus.REFUNDED;
            case PENDING    -> throw new IllegalStateException("not captured yet");
            case REFUNDED   -> throw new IllegalStateException("already refunded");
        }
    }

    public PaymentStatus status() { return status; }
}
