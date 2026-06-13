package behavioral.observer.good;

public class ObserverDemo {
    public static void main(String[] args) {
        System.out.println("== Observer [GOOD] ==");

        var publisher = new EventPublisher();

        // 1. Подписать 4 слушателя — PaymentService не меняется
        var receiptListener   = new ReceiptListener();
        var analyticsListener = new AnalyticsListener();
        var fraudListener     = new FraudAlertListener();

        publisher.subscribe(receiptListener);
        publisher.subscribe(analyticsListener);
        publisher.subscribe(fraudListener);
        // Добавить LedgerListener: 1 класс + 1 строка publisher.subscribe() — PaymentService не трогается
        publisher.subscribe(event ->
                System.out.println("ledger: recorded " + event.paymentId()));

        var paymentService = new PaymentService(publisher);

        // 2. Первый платёж: 4 слушателя получают событие
        System.out.println("-- платёж 1: 4 слушателя --");
        paymentService.pay("ord-101", 4900);

        // 3. Отписать AnalyticsListener (например, в integration-тестах или feature-flag)
        System.out.println();
        System.out.println("-- отписываем AnalyticsListener --");
        publisher.unsubscribe(analyticsListener);

        // 4. Второй платёж: только 3 слушателя — PaymentService об этом не знает
        System.out.println("-- платёж 2: 3 слушателя (analytics отписан) --");
        paymentService.pay("ord-102", 1500);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - добавить Slack: 1 класс SlackListener implements PaymentEventListener");
        System.out.println("    + publisher.subscribe(new SlackListener()) — PaymentService не меняется");
        System.out.println("  - unsubscribe: отключить слушатель без правки кода (feature flag, A/B)");
        System.out.println("  - unit-тест PaymentService требует только мок EventPublisher — 1 зависимость");
        System.out.println("  - в Spring: @EventListener на методе — подписка без кода вообще");
    }
}
