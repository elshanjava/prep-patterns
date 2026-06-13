package behavioral.memento.good;

import java.util.ArrayDeque;
import java.util.Deque;

// Caretaker хранит снимки как непрозрачные токены — не знает их содержимого.
// Добавить поля в Editor → History не меняется никогда.
// Переименовать Editor.content → History не замечает: она оперирует Snapshot, а не полями.
final class History {
    private final Deque<Editor.Snapshot> undoStack = new ArrayDeque<>();
    private final Deque<Editor.Snapshot> redoStack = new ArrayDeque<>();

    void backup(Editor e) {
        undoStack.push(e.save());
        redoStack.clear();  // новое действие сбрасывает redo-историю
    }

    void undo(Editor e) {
        if (undoStack.isEmpty()) return;
        redoStack.push(e.save());   // сохраняем текущее состояние в redo
        e.restore(undoStack.pop());
    }

    void redo(Editor e) {
        if (redoStack.isEmpty()) return;
        undoStack.push(e.save());
        e.restore(redoStack.pop());
    }
}
