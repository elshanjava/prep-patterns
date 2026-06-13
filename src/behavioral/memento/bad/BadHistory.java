package behavioral.memento.bad;

import java.util.ArrayDeque;
import java.util.Deque;

// Caretaker знает всю структуру Editor и читает/пишет публичные поля напрямую.
// Переименовать поле в Editor — сломается History.
final class BadHistory {
    private record Backup(String content, int cursor) {}

    private final Deque<Backup> stack = new ArrayDeque<>();

    void backup(BadEditor e) {
        stack.push(new Backup(e.content, e.cursor));  // прямой доступ к полям
    }

    void undo(BadEditor e) {
        if (stack.isEmpty()) return;
        var b = stack.pop();
        e.content = b.content();  // прямая запись в публичные поля
        e.cursor  = b.cursor();
    }
}
