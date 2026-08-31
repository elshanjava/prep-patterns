package pattern.coding.creational.abstractfactory;


import pattern.coding.creational.abstractfactory.model.Button;
import pattern.coding.creational.abstractfactory.model.Checkbox;
import pattern.coding.creational.abstractfactory.model.Scrollbar;

public interface UiFactory {
    Button createButton();
    Checkbox createCheckbox();
    Scrollbar createScrollbar();
}
