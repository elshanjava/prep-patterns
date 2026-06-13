package behavioral.memento;

import java.util.ArrayDeque;
import java.util.Deque;

// Caretaker хранит снимки как непрозрачные токены — не знает их содержимого.
final class History {
    private final Deque<Editor.Snapshot> stack = new ArrayDeque<>();

    void backup(Editor e) { stack.push(e.save()); }

    void undo(Editor e) {
        if (!stack.isEmpty()) e.restore(stack.pop());
    }
}
