package coding.structural.bridge;

public class SmsChannel implements Channel{

  @Override
  public void send(String msg) {
    System.out.println("Send message: " + msg + "  via sms channel");
  }
}
