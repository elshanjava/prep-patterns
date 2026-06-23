package coding.creational.abstractfactory.model;

public class LightThemeCheckbox implements Checkbox{

    @Override
    public void toggle() {
        System.out.println("LightThemeCheckbox toggle");
    }
}
