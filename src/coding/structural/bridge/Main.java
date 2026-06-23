package coding.structural.bridge;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    // Любой тип можно соединить с любым каналом — две оси независимы.
    List<NotificationType> notifications = List.of(
        new Alert(new EmailChannel()),    // тип Alert    + канал Email
        new Alert(new SmsChannel()),      // тот же Alert, другой канал
        new Urgent(new PushChannel()),    // тип Urgent   + канал Push
        new Reminder(new EmailChannel())  // тот же Email, другой тип
    );

    // Клиент дёргает один метод — тип решает ЧТО, канал решает КАК доставить.
    notifications.forEach(n -> n.send("Bob", "login"));
  }
}
