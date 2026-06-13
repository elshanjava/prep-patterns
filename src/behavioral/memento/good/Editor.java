package behavioral.memento.good;

// Все поля приватны — инкапсуляция не нарушена.
// Snapshot — непрозрачный токен: History не знает, что внутри.
// Добавить поле → расширить только Snapshot и save()/restore() внутри Editor.
// History не меняется никогда.
final class Editor {
    private String content        = "";
    private int    cursor         = 0;
    private int    selectionStart = 0;

    // Внутренний класс-снимок: содержит все 3 поля, но недоступен снаружи напрямую.
    // History хранит Snapshot как непрозрачный объект — не знает о его содержимом.
    public static final class Snapshot {
        private final String content;
        private final int    cursor;
        private final int    selectionStart;

        private Snapshot(String content, int cursor, int selectionStart) {
            this.content        = content;
            this.cursor         = cursor;
            this.selectionStart = selectionStart;
        }
    }

    public Snapshot save() {
        return new Snapshot(content, cursor, selectionStart);
    }

    public void restore(Snapshot s) {
        this.content        = s.content;
        this.cursor         = s.cursor;
        this.selectionStart = s.selectionStart;
    }

    public void type(String text) {
        content = content.substring(0, cursor) + text + content.substring(cursor);
        cursor += text.length();
    }

    public void select(int from, int to) {
        selectionStart = from;
        cursor         = to;
    }

    @Override
    public String toString() {
        return "\"" + content + "\" cursor=" + cursor + " sel=" + selectionStart;
    }
}
