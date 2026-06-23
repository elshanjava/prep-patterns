package coding.creational.abstractfactory.model;

public class DarkThemeCheckbox implements Checkbox {

    @Override
    public void toggle() {
        System.out.println("DarkThemeCheckbox toggle");
    }
}
