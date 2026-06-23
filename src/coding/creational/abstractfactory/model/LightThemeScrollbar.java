package coding.creational.abstractfactory.model;

public class LightThemeScrollbar implements Scrollbar {

    @Override
    public void scroll() {
        System.out.println("LightThemeScrollbar scroll");
    }
}
