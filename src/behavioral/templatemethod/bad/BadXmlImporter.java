package behavioral.templatemethod.bad;

import java.nio.file.Path;

// Тот же каркас что в BadCsvImporter — только parse отличается.
// Дублирование: рассинхронизировать open/validate/save/close легко.
final class BadXmlImporter {
    void run(Path file) {
        open(file);
        try {
            validate();
            parseXml();
            save();
        } finally {
            close();
        }
    }

    private void open(Path f) { System.out.println("open: " + f.getFileName()); }
    private void validate()   { System.out.println("validate: ok"); }
    private void parseXml()   { System.out.println("parse: XML"); }
    private void save()       { System.out.println("save"); }
    private void close()      { System.out.println("close"); }
}
