package coding.behavioral.templatemethod;

import java.util.List;

public class TemplateDemo {

  public static void main(String[] args) {
    List<ReportParser> reportParsers = List.of(new TsvParser(), new CsvParser());

    reportParsers.forEach(reportParser -> {
      reportParser.run(new Report("test"));
    });
  }
}
