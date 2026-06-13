package behavioral.command.good;

import java.util.ArrayList;
import java.util.List;

final class CommandBus {
    private final List<Command> journal = new ArrayList<>();

    void submit(Command cmd) {
        journal.add(cmd);
        retry(cmd);
    }

    private void retry(Command cmd) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                cmd.execute();
                return;
            } catch (RuntimeException e) {
                if (++attempts == 3) throw e;
            }
        }
    }

    List<Command> journal() { return List.copyOf(journal); }
}
