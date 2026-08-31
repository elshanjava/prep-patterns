package pattern.coding.creational.abstractfactory;

import pattern.coding.creational.abstractfactory.model.Button;
import pattern.coding.creational.abstractfactory.model.Checkbox;
import pattern.coding.creational.abstractfactory.model.DarkThemeButton;
import pattern.coding.creational.abstractfactory.model.DarkThemeCheckbox;
import pattern.coding.creational.abstractfactory.model.DarkThemeScrollbar;
import pattern.coding.creational.abstractfactory.model.Scrollbar;

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
