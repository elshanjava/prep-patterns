package structural.bridge.bad;

// 3-й уровень важности × 3 канала = нужен 9-й класс.
// Если бы было 4 канала (добавили WhatsApp) — уже 12 классов.
final class CriticalEmailNotification {
    void notify(String to, String text) {
        System.out.println("[email -> " + to + "] 🚨🚨 CRITICAL: " + text + " — IMMEDIATE ACTION REQUIRED");
    }
}
