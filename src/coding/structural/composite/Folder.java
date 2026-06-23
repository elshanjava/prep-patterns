package coding.structural.composite;

import java.util.ArrayList;
import java.util.List;

public class Folder implements FsNode {
  private final String name;
  private final List<FsNode> nodeList;

  public Folder (String name) {
    this.name = name;
    this.nodeList = new ArrayList<>();
  }

  public void add (FsNode node) {
    nodeList.add(node);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public long size() {
    return nodeList.stream()
        .mapToLong(FsNode::size)
        .sum();
  }
}
