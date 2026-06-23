package coding.creational.abstractfactory;

import coding.creational.abstractfactory.model.Button;
import coding.creational.abstractfactory.model.Checkbox;
import coding.creational.abstractfactory.model.DarkThemeButton;
import coding.creational.abstractfactory.model.DarkThemeCheckbox;
import coding.creational.abstractfactory.model.DarkThemeScrollbar;
import coding.creational.abstractfactory.model.Scrollbar;

public class DarkThemeFactory implements UiFactory{

    @Override
    public Button createButton() {
        return new DarkThemeButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new DarkThemeCheckbox();
    }

    @Override
    public Scrollbar createScrollbar() {
        return new DarkThemeScrollbar();
    }
}
