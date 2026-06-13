package behavioral.memento.bad;

// Поля публичны ради сохранения/восстановления — инкапсуляция сломана.
// Смена полей Editor ломает History и всех кто делает бэкап.
final class BadEditor {
    public String content = "";
    public int cursor = 0;

    public void type(String text) {
        content = content.substring(0, cursor) + text + content.substring(cursor);
        cursor += text.length();
    }

    @Override
    public String toString() {
        return "\"" + content + "\" cursor=" + cursor;
    }
}
