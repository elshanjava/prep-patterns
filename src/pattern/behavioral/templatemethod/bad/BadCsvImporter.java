package pattern.behavioral.templatemethod.bad;

import java.nio.file.Path;

// Каркас open→validate→parse→save→close продублирован в каждом классе.
// Исправить validate или close = правка всех импортёров.
final class BadCsvImporter {
    void run(Path file) {
        open(file);
        try {
            validate();
            parseCsv();
            save();
        } finally {
            close();
        }
    }

    private void open(Path f) { System.out.println("open: " + f.getFileName()); }
    private void validate()   { System.out.println("validate: ok"); }
    private void parseCsv()   { System.out.println("parse: CSV"); }
    private void save()       { System.out.println("save"); }
    private void close()      { System.out.println("close"); }
}
