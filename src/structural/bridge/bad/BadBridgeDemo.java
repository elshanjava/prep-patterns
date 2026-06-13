package structural.bridge.bad;

public class BadBridgeDemo {
    public static void main(String[] args) {
        System.out.println("== Bridge [BAD] — class explosion ==");
        System.out.println("6 классов для 2 типа × 3 канала. Добавить WhatsApp → +2 класса.");

        new UrgentEmailNotification().notify("a@x.io",  "Подозрительный вход");
        new UrgentSmsNotification().notify("+100",       "Подозрительный вход");
        new UrgentPushNotification().notify("device-7",  "Подозрительный вход");
        new NormalEmailNotification().notify("a@x.io",  "Платёж получен");
        new NormalSmsNotification().notify("+100",       "Платёж получен");
        new NormalPushNotification().notify("device-7",  "Платёж получен");

        System.out.println();
        System.out.println("Исправить формат '[СРОЧНО]' → правь 3 Urgent*-класса.");
        System.out.println("Добавить ScheduledNotification → ещё 3 новых класса.");
    }
}
