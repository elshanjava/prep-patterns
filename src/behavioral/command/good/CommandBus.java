package behavioral.command.good;

import java.util.ArrayList;
import java.util.List;

// CommandBus — посредник между вызывающим кодом и командами.
// Хранит журнал всех поданных команд; поддерживает retry с backoff.
final class CommandBus {
    private final List<Command> journal = new ArrayList<>();

    void submit(Command cmd) {
        journal.add(cmd);    // фиксируем факт подачи до выполнения
        retryWithBackoff(cmd, 3);
    }

    // Публичный retry: вызывающий код может явно повторить неудавшуюся команду.
    // В bad/ аналога нет: нечего ретраить — операция не объект.
    void retry(Command cmd, int maxAttempts) {
        retryWithBackoff(cmd, maxAttempts);
    }

    // Журнал всех поданных команд — для аудита, идемпотентности и replay.
    List<Command> journal() { return List.copyOf(journal); }

    // Откат последней команды из журнала
    void undoLast() {
        if (journal.isEmpty()) return;
        Command last = journal.get(journal.size() - 1);
        last.undo();
    }

    private void retryWithBackoff(Command cmd, int maxAttempts) {
        int attempts = 0;
        while (true) {
            try {
                cmd.execute();
                return;
            } catch (RuntimeException e) {
                if (++attempts >= maxAttempts) throw e;
                // В реальном коде здесь был бы Thread.sleep(backoffMs) или экспоненциальный бэкофф
                System.out.printf("  retry %d/%d после ошибки: %s%n", attempts, maxAttempts, e.getMessage());
            }
        }
    }
}
