package structural.bridge.bad;

public class BadBridgeDemo {
    public static void main(String[] args) {
        System.out.println("== Bridge [BAD] — M×N взрыв классов ==");
        System.out.println();
        System.out.println("3 уровня важности × 3 канала = 9 классов:");
        System.out.println("  NormalEmailNotification, NormalSmsNotification, NormalPushNotification");
        System.out.println("  UrgentEmailNotification, UrgentSmsNotification, UrgentPushNotification");
        System.out.println("  CriticalEmailNotification, CriticalSmsNotification, CriticalPushNotification");
        System.out.println();

        new NormalEmailNotification().notify("user@example.com", "платёж принят");
        new UrgentSmsNotification().notify("+79001234567",       "подозрительный вход");
        new CriticalPushNotification().notify("device-7",        "кошелёк скомпрометирован");
        new NormalSmsNotification().notify("+79001234567",       "перевод завершён");
        new UrgentEmailNotification().notify("ops@example.com",  "API latency > 1s");
        new CriticalEmailNotification().notify("cto@example.com","fraud ring detected");

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Добавить WhatsApp (4-й канал): +3 класса → итого 12");
        System.out.println("  Добавить Scheduled (4-й уровень): +4 класса → итого 16");
        System.out.println("  N каналов × M уровней = N×M классов");
        System.out.println("  Исправить форматирование '[СРОЧНО]': правь 3 Urgent*-класса, не 1");
        System.out.println("  Исправить '🚨🚨 CRITICAL': правь 3 Critical*-класса, не 1");
        System.out.println("  Тест Email-форматирования: нужно тестировать 3 класса, а не 1 метод");
    }
}
