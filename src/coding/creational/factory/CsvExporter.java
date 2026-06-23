package coding.creational.factory;

import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter implements FileExporter {

  @Override
  public String export(List<Row> rows) {
    String header = "id,name,amount";
    String body = rows.stream()
        .map(r -> r.id() + "," + r.name() + "," + r.amount())
        .collect(Collectors.joining("\n"));
    return header + "\n" + body;
  }
}
