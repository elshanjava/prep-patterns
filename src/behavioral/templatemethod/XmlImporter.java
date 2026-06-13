package behavioral.templatemethod;

import java.nio.file.Path;
import java.util.List;

final class XmlImporter extends FileImporter {
    @Override
    protected List<Row> parse(Path file) {
        System.out.println("parse: XML");
        return List.of(new Row("xml-row-1"));
    }
}
