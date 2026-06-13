package behavioral.memento.good;

public class MementoDemo {
    public static void main(String[] args) {
        System.out.println("== Memento [GOOD] ==");

        var editor  = new Editor();
        var history = new History();

        // 1. Ввод текста
        history.backup(editor);
        editor.type("Hello");
        System.out.println("after type:   " + editor);

        // 2. Ещё текст
        history.backup(editor);
        editor.type(", world");
        System.out.println("after type:   " + editor);

        // 3. Выделение — снимок включает cursor И selectionStart
        history.backup(editor);
        editor.select(0, 5);
        System.out.println("after select: " + editor);  // cursor=5, sel=0

        // 4. Undo: восстанавливает и cursor, и selectionStart
        history.undo(editor);
        System.out.println("after undo:   " + editor);  // sel=0 → до выделения

        // 5. Undo ещё раз
        history.undo(editor);
        System.out.println("after undo:   " + editor);

        // 6. Redo
        history.redo(editor);
        System.out.println("after redo:   " + editor);

        System.out.println();
        // Демонстрация: History не знает о полях Editor.
        // Если добавить поле 'zoom' в Editor — History не меняется вообще.
        System.out.println("Преимущества над bad:");
        System.out.println("  - History не знает имён полей Editor: rename/add поля → History не трогается");
        System.out.println("  - Snapshot приватный: случайная мутация editor.content снаружи невозможна");
        System.out.println("  - redo бесплатно: добавлен в History без изменения Editor");
        System.out.println("  - selectionStart восстанавливается вместе с cursor автоматически");
    }
}
