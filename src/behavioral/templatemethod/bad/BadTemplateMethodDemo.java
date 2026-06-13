package behavioral.templatemethod.bad;

import java.nio.file.Path;

public class BadTemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("== Template Method [BAD] ==");
        System.out.println("--- CSV ---");
        new BadCsvImporter().run(Path.of("transactions.csv"));
        System.out.println("--- XML ---");
        new BadXmlImporter().run(Path.of("transactions.xml"));
    }
}
