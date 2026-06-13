package behavioral.memento.bad;

public class BadMementoDemo {
    public static void main(String[] args) {
        System.out.println("== Memento [BAD] ==");

        var editor  = new BadEditor();
        var history = new BadHistory();

        history.backup(editor);
        editor.type("Hello");
        System.out.println("after type: " + editor);

        history.backup(editor);
        editor.type(", world");
        System.out.println("after type: " + editor);

        history.undo(editor);
        System.out.println("after undo: " + editor);
    }
}
