package coding.creational.abstractfactory;

public class Main {

    public static void main(String[] args) {
        // Выбор темы — единственное место, где упоминаются конкретные фабрики.
        // Переключить всю тему = поменять одну фабрику, клиент Application не трогаем.
        UiFactory factory = "dark".equals(System.getProperty("theme"))
                ? new DarkThemeFactory()
                : new LightThemeFactory();

        Application app = new Application(factory);

        System.out.println("=== тема: " + factory.getClass().getSimpleName() + " ===");
        app.renderUi();

        // Демонстрация: другая фабрика → всё семейство меняется согласованно
        System.out.println();
        System.out.println("=== принудительно DarkThemeFactory ===");
        new Application(new DarkThemeFactory()).renderUi();
    }
}
