package coding.behavioral.templatemethod;

public class TsvParser extends ReportParser{

  @Override
  protected void parse(Report report) {
    System.out.println("Parse TSV report");
  }
}
