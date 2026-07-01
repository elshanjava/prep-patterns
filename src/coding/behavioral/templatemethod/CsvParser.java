package coding.behavioral.templatemethod;

public class CsvParser extends ReportParser{

  @Override
  protected void parse(Report report) {
    System.out.println("Parse csv report");
  }
}
