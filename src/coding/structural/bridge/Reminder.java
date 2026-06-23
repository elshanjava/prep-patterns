package coding.structural.bridge;

public class Reminder extends NotificationType{

  Reminder(Channel channel) {
    super(channel);
  }

  @Override
  void send(String recipient, String event) {
    channel.send("Send reminder: " + event + "from: " + recipient);
  }
}
