package behavioral.memento.bad;

// Поля публичны ради сохранения/восстановления — инкапсуляция сломана.
// Смена имени поля в BadEditor ломает BadHistory и всех, кто делает бэкап.
// Добавление selectionStart: пришлось добавить 3-е публичное поле.
final class BadEditor {
    public String content        = "";
    public int    cursor         = 0;
    public int    selectionStart = 0;  // добавлено — теперь BadHistory должен знать о нём тоже

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
