package coding.behavioral.templatemethod;

public abstract class ReportParser {

  public final void run(Report report) {
    open(report);
    try {
      readLines(report);
      parse(report);
    } finally {
      close(report);
    }
  }

  protected void open(Report report) {
    System.out.println("Open report");
  }

  protected void readLines(Report report) {
    System.out.println("Read report");
  }

  protected void close(Report report) {
    System.out.println("Close report");
  }

  protected abstract void parse(Report report);


}
