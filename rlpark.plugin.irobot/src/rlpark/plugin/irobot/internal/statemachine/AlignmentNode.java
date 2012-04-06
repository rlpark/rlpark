package rlpark.plugin.irobot.internal.statemachine;

import rlpark.plugin.robot.internal.disco.drops.DropByteArray;

public class AlignmentNode implements SerialLinkNode {
  private final byte[] alignment;
  private int byteRead = 0;
  private final byte[] read;

  public AlignmentNode(int... header) {
    this(DropByteArray.toBytes(header));
  }

  public AlignmentNode(byte[] alignment) {
    this.alignment = alignment;
    read = new byte[alignment.length];
  }

  @Override
  public void start() {
    byteRead = 0;
  }

  @Override
  public void step(Byte b) {
    read[byteRead] = b;
    byteRead++;
  }

  @Override
  public boolean isDone() {
    return byteRead == alignment.length;
  }

  @Override
  public int sum() {
    return ChecksumNode.sum(read);
  }
}
