package structural.bridge.good;

public class BridgeDemo {
    public static void main(String[] args) {
        System.out.println("== Bridge [GOOD] — 2 абстракции + 3 канала = 5 классов вместо 6 ==");

        // Каналы создаются один раз — можно переиспользовать
        var email = new EmailChannel();
        var sms   = new SmsChannel();
        var push  = new PushChannel();

        // Любая комбинация без новых классов
        new NormalNotification(email).send("user@example.com", "платёж принят");
        new UrgentNotification(sms).send("+79001234567",       "подозрительная активность");
        new UrgentNotification(push).send("device-7",          "лимит превышен");
        new NormalNotification(push).send("device-9",          "перевод завершён");

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - было 6 классов (2×3), теперь 5 (2+3) — и это разница растёт");
        System.out.println("  - добавить WhatsApp: 1 класс WhatsAppChannel, 0 правок в Notification");
        System.out.println("  - добавить «критический» уровень: 1 класс CriticalNotification");
        System.out.println("  - исправить '[СРОЧНО]': правим только UrgentNotification — не 3 класса");
        System.out.println("  - при M типах × N каналах: M+N классов вместо M×N");
    }
}
