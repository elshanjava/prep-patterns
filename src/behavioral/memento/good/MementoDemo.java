package behavioral.memento.good;

public class MementoDemo {
    public static void main(String[] args) {
        System.out.println("== Memento [GOOD] ==");

        var editor  = new Editor();
        var history = new History();

        history.backup(editor);
        editor.type("Hello");
        System.out.println("after type: " + editor);

        history.backup(editor);
        editor.type(", world");
        System.out.println("after type: " + editor);

        history.undo(editor);
        System.out.println("after undo: " + editor);

        history.undo(editor);
        System.out.println("after undo: " + editor);
    }
}
