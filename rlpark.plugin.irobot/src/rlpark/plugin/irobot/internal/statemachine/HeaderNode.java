package rlpark.plugin.irobot.internal.statemachine;

import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import zephyr.plugin.core.api.signals.Signal;

public class HeaderNode implements SerialLinkNode {
  public final Signal<Integer> onMisalignedPackets = new Signal<Integer>();
  private final byte[] header;
  private final int checksum;
  private int checkedByte = 0;
  private int misalignedByte = 0;

  public HeaderNode(int... header) {
    this(DropByteArray.toBytes(header));
  }

  public HeaderNode(byte[] header) {
    this.header = header;
    checksum = ChecksumNode.sum(header);
  }

  @Override
  public void start() {
    misalignedByte = 0;
    checkedByte = 0;
  }

  @Override
  public void step(Byte b) {
    if (b == header[checkedByte])
      checkedByte++;
    else {
      // System.out.println(String.format("Received %d, expected %d", b,
      // header[checkedByte]));
      misalignedByte++;
      onMisalignedPackets.fire(misalignedByte);
      // if (misalignedByte % 1000 == 0)
      // System.err.println(String.format("Warning: %d misaligned packets",
      // misalignedByte));
      checkedByte = 0;
    }
  }

  @Override
  public boolean isDone() {
    boolean result = checkedByte == header.length;
    if (result && misalignedByte > 0)
      System.err.println(String.format("Warning: %d misaligned packets", misalignedByte));
    return result;
  }

  @Override
  public int sum() {
    return checksum;
  }
}
