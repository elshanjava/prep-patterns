package coding.creational.abstractfactory;


import coding.creational.abstractfactory.model.Button;
import coding.creational.abstractfactory.model.Checkbox;
import coding.creational.abstractfactory.model.Scrollbar;

public interface UiFactory {
    Button createButton();
    Checkbox createCheckbox();
    Scrollbar createScrollbar();
}
