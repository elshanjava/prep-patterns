package behavioral.templatemethod.good;

import java.nio.file.Path;
import java.util.List;

public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("== Template Method [GOOD] ==");

        // Все 3 импортёра используют один каркас из FileImporter
        List<FileImporter> importers = List.of(
                new CsvImporter(),
                new XmlImporter(),
                new JsonImporter()   // добавлен без правки FileImporter, CsvImporter, XmlImporter
        );

        for (FileImporter importer : importers) {
            String ext = importer.getClass().getSimpleName().replace("Importer", "").toLowerCase();
            System.out.println("--- " + ext.toUpperCase() + " ---");
            importer.run(Path.of("transactions." + ext));
        }

        System.out.println();
        // Если нужно исправить close() — добавить flush перед закрытием:
        //   → Правим только FileImporter.close()
        //   → CsvImporter, XmlImporter, JsonImporter получают исправление автоматически
        //   → В bad/ пришлось бы исправить 3 разных close()
        System.out.println("Преимущества над bad:");
        System.out.println("  - исправить close() = правь только FileImporter → все 3 получают исправление");
        System.out.println("  - добавить Parquet-формат = 1 класс ParquetImporter extends FileImporter");
        System.out.println("  - validate() переопределяется точечно: CsvImporter может ужесточить правила");
        System.out.println("  - каркас зафиксирован (final run()): порядок шагов нельзя нарушить случайно");
    }
}
