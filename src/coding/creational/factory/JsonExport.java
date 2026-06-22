package coding.creational.factory;

import java.util.List;
import java.util.stream.Collectors;

public class JsonExport implements FileExporter {

  @Override
  public String export(List<Row> rows) {
    return rows.stream()
        .map(r -> "  {\"id\": \"" + r.id() + "\", \"name\": \"" + r.name()
            + "\", \"amount\": " + r.amount() + "}")
        .collect(Collectors.joining(",\n", "[\n", "\n]"));
  }
}
