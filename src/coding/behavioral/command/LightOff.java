package coding.behavioral.command;

public class LightOff implements Command {

  private final Light light;

  public LightOff(Light light) {
    this.light = light;
  }

  @Override
  public void execute() {
    light.off();
  }

  @Override
  public void undo() {
    light.on();
  }
}
