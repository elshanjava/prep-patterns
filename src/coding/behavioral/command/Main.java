package coding.behavioral.command;

public class Main {

  public static void main(String[] args) {
    Light light = new Light();
    Stereo stereo = new Stereo();
    RemoteControl remote = new RemoteControl();

    System.out.println("=== Нажимаем кнопки ===");
    remote.press(new LightOn(light));     // Light is on
    remote.press(new VolumeUp(stereo));   // Volume up → 1
    remote.press(new LightOff(light));    // Light is off

    System.out.println();
    System.out.println("=== Undo (откат в обратном порядке) ===");
    remote.undoLast();   // отменяет LightOff  → Light is on
    remote.undoLast();   // отменяет VolumeUp  → Volume down → 0
    remote.undoLast();   // отменяет LightOn   → Light is off
  }
}
