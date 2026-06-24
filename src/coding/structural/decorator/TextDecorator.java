package coding.structural.decorator;

public abstract class TextDecorator implements TextProcessor{
    protected final TextProcessor wrapped;

    protected TextDecorator(TextProcessor wrapped) {
        this.wrapped = wrapped;
    }
}
