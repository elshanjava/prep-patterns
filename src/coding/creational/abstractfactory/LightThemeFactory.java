package coding.creational.abstractfactory;

import coding.creational.abstractfactory.model.Button;
import coding.creational.abstractfactory.model.Checkbox;
import coding.creational.abstractfactory.model.LightThemeButton;
import coding.creational.abstractfactory.model.LightThemeCheckbox;
import coding.creational.abstractfactory.model.LightThemeScrollbar;
import coding.creational.abstractfactory.model.Scrollbar;

public class LightThemeFactory implements UiFactory{
    @Override
    public Button createButton() {
        return new LightThemeButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new LightThemeCheckbox();
    }

    @Override
    public Scrollbar createScrollbar() {
        return new LightThemeScrollbar();
    }
}
