package behavioral.observer.good;

public class ObserverDemo {
    public static void main(String[] args) {
        System.out.println("== Observer [GOOD] ==");

        var publisher = new EventPublisher();
        publisher.subscribe(new ReceiptListener());
        publisher.subscribe(new AnalyticsListener());

        new PaymentService(publisher).pay("ord-101", 4900);
    }
}
