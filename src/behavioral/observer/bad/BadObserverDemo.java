package behavioral.observer.bad;

public class BadObserverDemo {
    public static void main(String[] args) {
        System.out.println("== Observer [BAD] ==");

        // 6 зависимостей в конструкторе: сразу видно нарушение SRP
        var service = new BadPaymentService(
                new EmailSender(),
                new Analytics(),
                new LedgerService(),
                new RiskService(),          // 4-я зависимость
                new FraudAlertService(),    // 5-я зависимость
                new KafkaProducer()         // 6-я зависимость
        );

        service.pay("ord-101", 4900);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - добавить Slack = 7-я зависимость + правка pay() + правка конструктора");
        System.out.println("  - unit-тест pay() требует 6 моков — только чтобы проверить 1 строку логики");
        System.out.println("  - отключить аналитику в prod = закомментировать вызов в pay() → регрессия");
        System.out.println("  - если KafkaProducer бросит исключение — email и ledger уже выполнились");
    }
}
