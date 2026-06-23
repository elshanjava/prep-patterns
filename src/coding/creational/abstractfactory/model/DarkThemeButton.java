package coding.creational.abstractfactory.model;

public class DarkThemeButton implements Button {

    @Override
    public void click() {
        System.out.println("Dark Theme Button clicked");
    }
}
