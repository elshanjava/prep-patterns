package structural.bridge.bad;

final class NormalPushNotification {
    void notify(String to, String text) {
        System.out.println("[push -> " + to + "] " + text);
    }
}
