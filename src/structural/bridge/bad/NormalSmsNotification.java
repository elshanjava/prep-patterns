package structural.bridge.bad;

final class NormalSmsNotification {
    void notify(String to, String text) {
        System.out.println("[sms -> " + to + "] " + text);
    }
}
