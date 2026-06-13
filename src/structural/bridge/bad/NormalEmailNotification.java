package structural.bridge.bad;

final class NormalEmailNotification {
    void notify(String to, String text) {
        System.out.println("[email -> " + to + "] " + text);
    }
}
