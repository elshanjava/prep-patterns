package coding.structural.decorator;

public class ToUpperCase extends TextDecorator {

    public ToUpperCase(TextProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String input) {
        return wrapped.process(input).toUpperCase();
    }
}
