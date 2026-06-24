package coding.structural.decorator;

public class PlainText implements TextProcessor {

    @Override
    public String process(String input) {
        return input;
    }
}
