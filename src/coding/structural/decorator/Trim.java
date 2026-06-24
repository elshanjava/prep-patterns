package coding.structural.decorator;

public class Trim extends TextDecorator {

    public Trim(TextProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String input) {
        return wrapped.process(input).trim();
    }
}
