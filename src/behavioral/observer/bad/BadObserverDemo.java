package behavioral.observer.bad;

public class BadObserverDemo {
    public static void main(String[] args) {
        System.out.println("== Observer [BAD] ==");
        var service = new BadPaymentService(new EmailSender(), new Analytics(), new LedgerService());
        service.pay("ord-101", 4900);
    }
}
