package coding.creational.factory;

import java.util.Map;
import java.util.function.Supplier;

public class FileExporterFactory {

  private static final Map<String, Supplier<FileExporter>> FACTORY = Map.of(
      "CSV", CsvExport::new,
      "JSON", JsonExport::new,
      "XML", XmlExporter::new
  );

  public FileExporter create(String format) {

    var fileExporterSupplier = FACTORY.get(format);
    if (fileExporterSupplier == null) {
      throw new IllegalArgumentException("unknown format: " + format);
    }

    return fileExporterSupplier.get();

  }
}
