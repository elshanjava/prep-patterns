package behavioral.observer.good;

import behavioral.observer.model.PaymentCompleted;

// Новый подписчик: добавляется без правки PaymentService или EventPublisher.
// В Spring: @Component + @EventListener — автоматически подхватывается контейнером.
final class FraudAlertListener implements PaymentEventListener {
    public void on(PaymentCompleted e) {
        System.out.println("fraudAlert: checking payment " + e.paymentId() + " amount=" + e.amount());
    }
}
