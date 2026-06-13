package behavioral.templatemethod.good;

import behavioral.templatemethod.model.Row;

import java.nio.file.Path;
import java.util.List;

abstract class FileImporter {

    // Каркас фиксирован (final); наследник меняет только шаг parse.
    public final void run(Path file) {
        open(file);
        try {
            validate();
            var data = parse(file);
            save(data);
        } finally {
            close();
        }
    }

    protected void validate() { System.out.println("validate: ok"); }

    protected abstract List<Row> parse(Path file);

    protected void save(List<Row> data) { System.out.println("save: " + data.size() + " rows"); }

    private void open(Path f) { System.out.println("open: " + f.getFileName()); }
    private void close()      { System.out.println("close"); }
}
