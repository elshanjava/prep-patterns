package structural.bridge;

final class PushChannel  implements MessageChannel {
  public void send(String to, String b){ System.out.println("  [push -> " + to + "] " + b); }
}