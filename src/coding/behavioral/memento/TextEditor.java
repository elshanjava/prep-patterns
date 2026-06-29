package coding.behavioral.memento;

public class TextEditor {
    private String content = "";

    public void type(String text) {
        content += text;
    }

    public String content() {
        return content;
    }

    public Memento save() {
        return new Memento(content);
    }

    public void restore(Memento memento) {
        this.content = memento.content;
    }

    public static final class Memento {
        private final String content;

        private Memento(String content) {
            this.content = content;
        }
    }
}
