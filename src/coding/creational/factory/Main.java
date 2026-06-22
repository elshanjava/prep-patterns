package coding.creational.factory;

import java.math.BigDecimal;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    List<Row> rows = List.of(
        new Row("1", "Alice", new BigDecimal("100")),
        new Row("2", "Bob", new BigDecimal("50"))
    );

    var factory = new FileExporterFactory();

    // Клиент не знает ни одного конкретного класса — только строку формата и FileExporter.
    for (String format : List.of("CSV", "JSON", "XML")) {
      FileExporter exporter = factory.create(format);   // фабрика дала продукт
      String out = exporter.export(rows);               // продукт сделал текст
      System.out.println("=== " + format + " ===");
      System.out.println(out);
      System.out.println();
    }

    // Неизвестный формат → понятная ошибка
    try {
      factory.create("PDF");
    } catch (IllegalArgumentException e) {
      System.out.println("ожидаемая ошибка: " + e.getMessage());
    }
  }
}
