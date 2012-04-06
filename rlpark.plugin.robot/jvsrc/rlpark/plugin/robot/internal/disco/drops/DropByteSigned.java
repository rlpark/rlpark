package rlpark.plugin.robot.internal.disco.drops;


import rlpark.plugin.rltoys.math.GrayCode;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.internal.disco.datatype.ScalarReader;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropByteSigned extends DropData implements ScalarReader, GrayCodeConverter {
  private byte value;

  public DropByteSigned(String label) {
    this(label, -1);
  }

  public DropByteSigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropByteSigned(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.get(index);
  }

  @Override
  public void convert(LiteByteBuffer source, LiteByteBuffer target) {
    value = (byte) getInt(source);
    value = GrayCode.byteToGrayCode(value);
    putData(target);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.put(value);
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Range range() {
    return new Range(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }
}
