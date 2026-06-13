package behavioral.templatemethod.bad;

import java.nio.file.Path;

// 3-й импортёр: тот же каркас open→validate→parse→save→close скопирован в 3-й раз.
// open(), validate(), save(), close() — идентичные copy-paste из BadCsvImporter и BadXmlImporter.
// Исправить close() (например, добавить flush перед закрытием) = правь 3 класса.
// Забыть один = рассинхронизация поведения без ошибки компиляции.
final class BadJsonImporter {
    void run(Path file) {
        open(file);
        try {
            validate();    // дублирование: то же, что в BadCsvImporter и BadXmlImporter
            parseJson();   // единственный отличающийся метод
            save();        // дублирование
        } finally {
            close();       // дублирование: тот же close что в двух других классах
        }
    }

    private void open(Path f) { System.out.println("open: " + f.getFileName()); }  // copy-paste
    private void validate()   { System.out.println("validate: ok"); }               // copy-paste
    private void parseJson()  { System.out.println("parse: JSON"); }               // единственное отличие
    private void save()       { System.out.println("save"); }                       // copy-paste
    private void close()      { System.out.println("close"); }                      // copy-paste
}
