package coding.behavioral.memento;

public class Main {

    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        History history = new History();

        editor.type("Hello");
        history.push(editor.save());          // snapshot #1: "Hello"
        System.out.println("после ввода 1: '" + editor.content() + "'");

        editor.type(", world");
        history.push(editor.save());          // snapshot #2: "Hello, world"
        System.out.println("после ввода 2: '" + editor.content() + "'");

        editor.type("!!! опечатка");
        System.out.println("после ввода 3: '" + editor.content() + "'");

        // --- undo: откатываемся к последнему сохранённому снимку ---
        System.out.println();
        System.out.println("-- undo --");
        editor.restore(history.pop());        // вернётся "Hello, world"
        System.out.println("после undo 1: '" + editor.content() + "'");

        editor.restore(history.pop());        // вернётся "Hello"
        System.out.println("после undo 2: '" + editor.content() + "'");

        // --- история пуста: pop() вернёт null, не упадёт ---
        System.out.println();
        System.out.println("-- история пуста --");
        TextEditor.Memento m = history.pop();
        System.out.println("pop() на пустой истории -> " + m);
    }
}
