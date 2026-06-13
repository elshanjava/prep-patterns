package behavioral.observer;

public class ObserverDemo {
    public static void main(String[] args) {
        System.out.println("== Observer ==");

        var publisher = new EventPublisher();
        // подписчики регистрируются независимо — PaymentService не меняется (OCP)
        publisher.subscribe(new ReceiptListener());
        publisher.subscribe(new AnalyticsListener());

        var service = new PaymentService(publisher);
        service.pay("ord-101", 4900);
    }
}
