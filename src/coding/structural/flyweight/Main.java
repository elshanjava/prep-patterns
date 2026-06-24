package coding.structural.flyweight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TreeTypeFactory factory = new TreeTypeFactory();
        Random rnd = new Random(42);

        // Сажаем большой лес: 1_000_000 деревьев, но всего 2 ВИДА
        List<Tree> forest = new ArrayList<>();
        for (int i = 0; i < 1_000_000; i++) {
            int x = rnd.nextInt(1000);
            int y = rnd.nextInt(1000);

            // у каждого дерева своя координата (extrinsic),
            // но тип берётся из фабрики — общий объект (intrinsic)
            Tree tree = (i % 2 == 0)
                    ? new Tree(x, y, factory.getTreeType("Берёза", "белый", "texture-birch"))
                    : new Tree(x, y, factory.getTreeType("Дуб", "зелёный", "texture-oak"));
            forest.add(tree);
        }

        System.out.println("Деревьев посажено:        " + forest.size());
        System.out.println("Объектов TreeType создано: " + factory.cacheSize());
        System.out.println("  (миллион деревьев → всего " + factory.cacheSize() + " общих типа — вот экономия памяти)");

        // Доказательство: повторный запрос того же вида возвращает ТОТ ЖЕ объект
        TreeType birch1 = factory.getTreeType("Берёза", "белый", "texture-birch");
        TreeType birch2 = factory.getTreeType("Берёза", "белый", "texture-birch");
        System.out.println();
        System.out.println("birch1 == birch2 (один и тот же объект)? " + (birch1 == birch2));

        // Рисуем пару деревьев — extrinsic-координаты приходят снаружи в общий flyweight
        System.out.println();
        forest.get(0).draw();
        forest.get(1).draw();
    }
}
