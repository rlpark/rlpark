package rlpark.plugin.robot.internal.disco.drops;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;



public class DropEndBit extends DropData {
  public DropEndBit(String label) {
    super(label, true);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropEndBit(label);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
  }

  @Override
  public int size() {
    return ByteSize;
  }

}
