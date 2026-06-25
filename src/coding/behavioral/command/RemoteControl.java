package coding.behavioral.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class RemoteControl {

  private final Deque<Command> history = new ArrayDeque<>();

  public void press(Command cmd) {
    cmd.execute();
    history.push(cmd);
  }
  public void undoLast() {
    if (!history.isEmpty()) history.pop().undo();   // откат последней (LIFO)
  }
}
