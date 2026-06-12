package structural.bridge;

// Запуск: две независимые оси — «вид уведомления» (абстракция) и «канал
// доставки» (реализация) — свободно комбинируются. M абстракций × N каналов
// дают M*N сочетаний без M*N классов.
public class BridgeDemo {
    public static void main(String[] args) {
        System.out.println("== Bridge ==");
        new NormalNotification(new EmailChannel()).notify("a@x.io",   "Платёж получен");
        new UrgentNotification(new SmsChannel()).notify("+100",       "Подозрительный вход");
        new UrgentNotification(new PushChannel()).notify("device-7",  "OTP 123456");
    }
}
