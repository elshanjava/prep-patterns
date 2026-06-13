package structural.bridge.bad;

final class UrgentPushNotification {
    void notify(String to, String text) {
        System.out.println("[push -> " + to + "] [СРОЧНО] " + text);
    }
}
