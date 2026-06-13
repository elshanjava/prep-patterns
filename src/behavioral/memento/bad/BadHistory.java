package behavioral.memento.bad;

import java.util.ArrayDeque;
import java.util.Deque;

// Caretaker знает ВСЮ структуру BadEditor: читает и пишет публичные поля напрямую.
// Переименовать BadEditor.content → text? → BadHistory тоже ломается.
// Добавить поле selectionStart в BadEditor → нужно добавить его и сюда.
final class BadHistory {
    // Backup знает о 3 конкретных полях BadEditor — жёсткая связь
    private record Backup(String content, int cursor, int selectionStart) {}

    private final Deque<Backup> stack = new ArrayDeque<>();

    void backup(BadEditor e) {
        // прямой доступ к публичным полям: BadHistory знает детали реализации BadEditor
        stack.push(new Backup(e.content, e.cursor, e.selectionStart));
    }

    void undo(BadEditor e) {
        if (stack.isEmpty()) return;
        var b = stack.pop();
        // прямая запись в публичные поля: rename BadEditor.content → BadHistory сломается
        e.content        = b.content();
        e.cursor         = b.cursor();
        e.selectionStart = b.selectionStart();
    }
}
