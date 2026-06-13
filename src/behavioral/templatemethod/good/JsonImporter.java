package behavioral.templatemethod.good;

import behavioral.templatemethod.model.Row;

import java.nio.file.Path;
import java.util.List;

// Добавление нового формата = 1 новый класс, только метод parse().
// open(), validate(), save(), close() наследуются из FileImporter — не дублируются.
// Исправить close() в FileImporter → JsonImporter, CsvImporter, XmlImporter получат исправление автоматически.
final class JsonImporter extends FileImporter {
    @Override
    protected List<Row> parse(Path file) {
        System.out.println("parse: JSON");
        return List.of(new Row("json-row-1"), new Row("json-row-2"), new Row("json-row-3"));
    }
}
