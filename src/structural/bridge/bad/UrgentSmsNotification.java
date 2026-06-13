package structural.bridge.bad;

final class UrgentSmsNotification {
    void notify(String to, String text) {
        System.out.println("[sms -> " + to + "] [СРОЧНО] " + text);
    }
}
