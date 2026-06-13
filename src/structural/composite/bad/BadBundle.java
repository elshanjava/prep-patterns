package structural.composite.bad;

import java.util.List;

// Контейнер без интерфейса: BadOrderCalculator обязан знать о нём и о BadLineItem,
// чтобы делать instanceof-ветвления вручную в каждом методе.
final class BadBundle {
    private final String       name;
    private final List<Object> items;

    BadBundle(String name, List<Object> items) {
        this.name  = name;
        this.items = List.copyOf(items);
    }

    String       name()  { return name; }
    List<Object> items() { return items; }
}
