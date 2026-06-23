package coding.creational.prototype;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        Shape original = new Shape("red", 10, source);

        // 1. Защитная копия в конструкторе: меняем исходный список ПОСЛЕ создания
        source.add("c");
        System.out.println("original после мутации source: " + original.getTags());
        System.out.println("  (ожидаем [a, b] — конструктор скопировал список)");

        // 2. copy() создаёт независимый клон с тем же содержимым
        Shape clone = original.copy();
        System.out.println();
        System.out.println("original tags: " + original.getTags());
        System.out.println("clone tags:    " + clone.getTags());
        System.out.println("содержимое равно? " + original.getTags().equals(clone.getTags()));

        // 3. Геттер теперь read-only — попытка изменить падает
        System.out.println();
        try {
            clone.getTags().add("hack");
        } catch (UnsupportedOperationException e) {
            System.out.println("геттер защищён: изменить список снаружи нельзя (UnsupportedOperationException)");
        }

        // 4. Иммутабельные поля скопированы корректно
        System.out.println();
        System.out.println("clone: color=" + clone.getColor() + ", radius=" + clone.getRadius());
    }
}
