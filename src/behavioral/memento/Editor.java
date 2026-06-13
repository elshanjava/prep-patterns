package behavioral.memento;

final class Editor {
    private String content = "";
    private int cursor = 0;

    public static final class Snapshot {
        private final String content;
        private final int cursor;
        private Snapshot(String content, int cursor) {
            this.content = content;
            this.cursor = cursor;
        }
    }

    public Snapshot save() {
        return new Snapshot(content, cursor);
    }

    public void restore(Snapshot s) {
        this.content = s.content;
        this.cursor = s.cursor;
    }

    public void type(String text) {
        content = content.substring(0, cursor) + text + content.substring(cursor);
        cursor += text.length();
    }

    @Override
    public String toString() {
        return "\"" + content + "\" cursor=" + cursor;
    }
}
