package behavioral.templatemethod.good;

import java.nio.file.Path;

public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("== Template Method [GOOD] ==");
        System.out.println("--- CSV ---");
        new CsvImporter().run(Path.of("transactions.csv"));
        System.out.println("--- XML ---");
        new XmlImporter().run(Path.of("transactions.xml"));
    }
}
