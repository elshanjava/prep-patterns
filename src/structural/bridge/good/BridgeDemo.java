package structural.bridge.good;

public class BridgeDemo {
    public static void main(String[] args) {
        System.out.println("== Bridge [GOOD] — M+N классов вместо M×N ==");
        System.out.println();
        System.out.println("3 уровня + 4 канала = 3+4 = 7 классов (было бы 3×4 = 12 в bad-варианте)");
        System.out.println();

        var email    = new EmailChannel();
        var sms      = new SmsChannel();
        var push     = new PushChannel();
        var whatsApp = new WhatsAppChannel();   // 4-й канал: 1 класс, 0 изменений в Notification

        // Все 3 уровня × 4 канала — любая комбинация без новых классов
        new NormalNotification(email).send("user@example.com",  "платёж принят");
        new UrgentNotification(sms).send("+79001234567",        "подозрительный вход");
        new CriticalNotification(push).send("device-7",         "кошелёк скомпрометирован");
        new CriticalNotification(whatsApp).send("+44700000001", "fraud ring detected");
        new NormalNotification(whatsApp).send("+44700000002",   "перевод завершён");
        new UrgentNotification(email).send("ops@example.com",   "API latency > 1s");

        System.out.println();
        System.out.println("Исправить '🚨🚨 CRITICAL': правим только CriticalNotification — 1 класс");
        System.out.println("Добавить Telegram: TelegramChannel — 1 класс, все 3 уровня сразу работают");
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  было 9 классов (3×3), стало 7 (3+4) — и разрыв растёт с каждым добавлением");
        System.out.println("  добавить WhatsApp: 1 класс (WhatsAppChannel), 0 изменений в Notification");
        System.out.println("  добавить Scheduled-уровень: 1 класс (ScheduledNotification), 0 изменений в каналах");
        System.out.println("  тест Email-форматирования: тестируем EmailChannel один раз, не 3 раза");
    }
}
