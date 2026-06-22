package coding.creational.factory;

import java.util.List;
import java.util.stream.Collectors;

public class XmlExporter implements FileExporter {

  @Override
  public String export(List<Row> rows) {
    return rows.stream()
        .map(r -> "  <row id=\"" + r.id() + "\" name=\"" + r.name()
            + "\" amount=\"" + r.amount() + "\"/>")
        .collect(Collectors.joining("\n", "<rows>\n", "\n</rows>"));
  }
}
