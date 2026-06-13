package behavioral.observer.bad;

final class FraudAlertService {
    void notify(String orderId, long amount) {
        System.out.println("fraudAlert: notified for " + orderId);
    }
}
