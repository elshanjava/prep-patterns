package behavioral.memento.bad;

public class BadMementoDemo {
    public static void main(String[] args) {
        System.out.println("== Memento [BAD] ==");

        var editor  = new BadEditor();
        var history = new BadHistory();

        // 1. начальный снимок
        history.backup(editor);
        editor.type("Hello");
        System.out.println("after type:   " + editor);

        // 2. снимок с текстом
        history.backup(editor);
        editor.type(", world");
        System.out.println("after type:   " + editor);

        // 3. выделение (selectionStart меняется)
        history.backup(editor);
        editor.select(0, 5);  // выделить "Hello"
        System.out.println("after select: " + editor);

        // 4. undo: BadHistory напрямую пишет в поля BadEditor
        history.undo(editor);
        System.out.println("after undo:   " + editor);  // cursor и selectionStart восстановлены

        history.undo(editor);
        System.out.println("after undo:   " + editor);

        System.out.println();
        // Жёсткая связь: BadHistory знает имена полей BadEditor.
        // Любое рефакторинг-действие (rename, split, merge полей) требует правки BadHistory.
        System.out.println("Проблемы:");
        System.out.println("  - BadHistory знает имена полей BadEditor: content, cursor, selectionStart");
        System.out.println("  - rename BadEditor.content → text: BadHistory ломается на компиляции");
        System.out.println("  - добавить поле version в BadEditor: нужно добавить в Backup тоже");
        System.out.println("  - поля публичны: любой код может случайно изменить editor.content напрямую");
    }
}
