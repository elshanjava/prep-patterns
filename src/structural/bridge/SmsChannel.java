package structural.bridge;

final class SmsChannel   implements MessageChannel {
  public void send(String to, String b){ System.out.println("  [sms -> " + to + "] " + b); }
}