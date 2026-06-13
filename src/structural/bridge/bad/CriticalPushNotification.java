package structural.bridge.bad;

final class CriticalPushNotification {
    void notify(String to, String text) {
        System.out.println("[push -> " + to + "] 🚨🚨 CRITICAL: " + text);
    }
}
