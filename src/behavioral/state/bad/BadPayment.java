package behavioral.state.bad;

// Логика переходов размазана по switch-блокам в каждом методе.
// Добавить статус CAPTURED = редактировать switch в capture() + refund() + cancel().
// Легко пропустить кейс → молчаливый баг (default не бросает исключение).
final class BadPayment {
    private PaymentStatus status = PaymentStatus.PENDING;

    // Switch 1: метод capture
    public void capture() {
        switch (status) {
            case PENDING    -> status = PaymentStatus.AUTHORIZED;
            // CAPTURED добавлен — нужно обработать здесь явно
            case AUTHORIZED -> throw new IllegalStateException("already authorized");
            case CAPTURED   -> throw new IllegalStateException("already captured");
            case REFUNDED   -> throw new IllegalStateException("already refunded");
        }
    }

    // Switch 2: метод refund — тот же список статусов, другая логика
    public void refund() {
        switch (status) {
            case AUTHORIZED -> status = PaymentStatus.REFUNDED;
            // CAPTURED добавлен — снова нужно обработать здесь
            case CAPTURED   -> status = PaymentStatus.REFUNDED;  // refund возможен из CAPTURED
            case PENDING    -> throw new IllegalStateException("not captured yet");
            case REFUNDED   -> throw new IllegalStateException("already refunded");
        }
    }

    // Switch 3: метод cancel — третий switch, снова со всеми статусами
    // Добавление CAPTURED = правь все 3 switch'а. Забыть один = молчаливый баг.
    public void cancel() {
        switch (status) {
            case PENDING    -> status = PaymentStatus.REFUNDED;   // отмена до capture
            case AUTHORIZED -> status = PaymentStatus.REFUNDED;   // отмена до charge
            case CAPTURED   -> throw new IllegalStateException("captured: refund instead of cancel");
            case REFUNDED   -> throw new IllegalStateException("already refunded");
        }
    }

    public PaymentStatus status() { return status; }
}
