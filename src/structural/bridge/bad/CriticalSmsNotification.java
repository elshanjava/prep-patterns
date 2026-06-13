package structural.bridge.bad;

final class CriticalSmsNotification {
    void notify(String to, String text) {
        System.out.println("[sms -> " + to + "] 🚨🚨 CRITICAL: " + text);
    }
}
