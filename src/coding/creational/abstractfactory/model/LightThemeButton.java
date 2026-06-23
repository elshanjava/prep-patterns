package coding.creational.abstractfactory.model;

public class LightThemeButton implements Button {

    @Override
    public void click() {
        System.out.println("LightThemeButton clicked");
    }
}
