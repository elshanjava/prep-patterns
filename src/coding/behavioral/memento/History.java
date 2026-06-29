package coding.behavioral.memento;

import java.util.Deque;
import java.util.ArrayDeque;

public class History {
    private final Deque<TextEditor.Memento> snapshots = new ArrayDeque<>();

    public void push(TextEditor.Memento memento) {
        snapshots.push(memento);
    }

    // отдать последний снимок для undo (или null, если истории нет)
    public TextEditor.Memento pop() {
        if (snapshots.isEmpty()) {
            return null;
        }
        return snapshots.pop();
    }

    public boolean isEmpty() {
        return snapshots.isEmpty();
    }
}
