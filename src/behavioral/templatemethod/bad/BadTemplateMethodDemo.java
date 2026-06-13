package behavioral.templatemethod.bad;

import java.nio.file.Path;

public class BadTemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("== Template Method [BAD] ==");

        System.out.println("--- CSV ---");
        new BadCsvImporter().run(Path.of("transactions.csv"));

        System.out.println("--- XML ---");
        new BadXmlImporter().run(Path.of("transactions.xml"));

        System.out.println("--- JSON ---");
        new BadJsonImporter().run(Path.of("transactions.json"));

        System.out.println();
        // open(), validate(), save(), close() скопированы в 3 разных класса.
        // Все 3 вывода выглядят одинаково — потому что код идентичен (дублирование).
        // Теперь:
        //   1. Нужно добавить "flush" перед close() для корректного сброса буфера в файл.
        //      → Правь BadCsvImporter.close() + BadXmlImporter.close() + BadJsonImporter.close()
        //      → Забыть один = непоследовательное поведение
        //   2. Добавить 4-й формат (Parquet, Avro) = снова скопировать каркас целиком
        System.out.println("Проблемы:");
        System.out.println("  - исправить close() = правь 3 класса (CSV + XML + JSON)");
        System.out.println("  - open/validate/save/close: 3×5=15 строк дублирования");
        System.out.println("  - добавить 4-й формат = скопировать каркас целиком ещё раз");
        System.out.println("  - рассинхронизация validate() между классами: разные правила без ошибки");
    }
}
