package structural.bridge.bad;

// Взрыв классов без Bridge: M типов × N каналов = M*N классов.
// Сейчас: 2 типа × 3 канала = 6 классов (и это ещё маленький пример).
// Добавить WhatsApp? +2 класса. Добавить «Scheduled»-тип? +3 класса.
// Исправить опечатку в "[email]"? — правь UrgentEmailNotification + NormalEmailNotification.
final class UrgentEmailNotification {
    void notify(String to, String text) {
        System.out.println("[email -> " + to + "] [СРОЧНО] " + text);
    }
}
