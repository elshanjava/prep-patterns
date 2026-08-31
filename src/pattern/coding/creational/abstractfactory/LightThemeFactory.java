package pattern.coding.creational.abstractfactory;

import pattern.coding.creational.abstractfactory.model.Button;
import pattern.coding.creational.abstractfactory.model.Checkbox;
import pattern.coding.creational.abstractfactory.model.LightThemeButton;
import pattern.coding.creational.abstractfactory.model.LightThemeCheckbox;
import pattern.coding.creational.abstractfactory.model.LightThemeScrollbar;
import pattern.coding.creational.abstractfactory.model.Scrollbar;

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
