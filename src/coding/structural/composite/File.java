package coding.structural.composite;

public record File(String fileName, long sizeValue) implements FsNode {

  @Override
  public String name() {
    return fileName;
  }

  @Override
  public long size() {
    return sizeValue;
  }
}
