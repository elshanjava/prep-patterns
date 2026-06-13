package behavioral.templatemethod;

import java.nio.file.Path;
import java.util.List;

final class CsvImporter extends FileImporter {
    @Override
    protected List<Row> parse(Path file) {
        System.out.println("parse: CSV");
        return List.of(new Row("csv-row-1"), new Row("csv-row-2"));
    }
}
