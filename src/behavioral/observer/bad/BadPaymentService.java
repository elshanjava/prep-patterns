package behavioral.observer.bad;

// Издатель знает всех потребителей поимённо.
// Новый потребитель = правка этого класса + конструктора (нарушение OCP).
// После 6 зависимостей unit-тест требует 6 моков — только чтобы проверить pay().
final class BadPaymentService {
    private final EmailSender    email;
    private final Analytics      analytics;
    private final LedgerService  ledger;
    private final RiskService    risk;         // 4-я зависимость
    private final FraudAlertService fraudAlert; // 5-я зависимость
    private final KafkaProducer  kafka;         // 6-я зависимость

    BadPaymentService(EmailSender email, Analytics analytics, LedgerService ledger,
                      RiskService risk, FraudAlertService fraudAlert, KafkaProducer kafka) {
        this.email      = email;
        this.analytics  = analytics;
        this.ledger     = ledger;
        this.risk       = risk;
        this.fraudAlert = fraudAlert;
        this.kafka      = kafka;
    }

    void pay(String orderId, long amount) {
        System.out.println("payment: processing " + orderId);
        email.sendReceipt(orderId, amount);      // 1. квитанция
        analytics.track(orderId, amount);        // 2. аналитика
        ledger.record(orderId, amount);          // 3. бухгалтерия
        risk.evaluate(orderId, amount);          // 4. оценка риска
        fraudAlert.notify(orderId, amount);      // 5. фрод-алерт
        kafka.publish("payment.completed",       // 6. публикация в Kafka
                      orderId, amount);
        // Добавить Slack-уведомление = 7-я зависимость + 7-й вызов здесь
        // Каждая правка этого метода несёт риск регрессии для всех остальных потребителей
    }
}
