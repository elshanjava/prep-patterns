package coding.creational.abstractfactory;

import coding.creational.abstractfactory.model.Button;
import coding.creational.abstractfactory.model.Checkbox;
import coding.creational.abstractfactory.model.Scrollbar;

// Клиент зависит ТОЛЬКО от UiFactory — не знает ни одного конкретного класса темы.
// Все три компонента приходят из одной фабрики → смешать Light и Dark невозможно.
public class Application {

    private final Button    button;
    private final Checkbox  checkbox;
    private final Scrollbar scrollbar;

    public Application(UiFactory factory) {
        this.button    = factory.createButton();
        this.checkbox  = factory.createCheckbox();
        this.scrollbar = factory.createScrollbar();
    }

    public void renderUi() {
        button.click();
        checkbox.toggle();
        scrollbar.scroll();
    }
}
