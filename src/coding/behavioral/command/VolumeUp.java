package coding.behavioral.command;

public class VolumeUp implements Command {

  private final Stereo stereo;

  public VolumeUp(Stereo stereo) {
    this.stereo = stereo;
  }

  @Override
  public void execute() {
    stereo.up();
  }

  @Override
  public void undo() {
    stereo.down();
  }
}
