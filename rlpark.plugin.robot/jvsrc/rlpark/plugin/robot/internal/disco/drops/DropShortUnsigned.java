package rlpark.plugin.robot.internal.disco.drops;


import rlpark.plugin.rltoys.math.GrayCode;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.internal.disco.datatype.ScalarReader;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropShortUnsigned extends DropData implements ScalarReader, GrayCodeConverter {
  private int value;

  public DropShortUnsigned(String label) {
    this(label, -1);
  }

  public DropShortUnsigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropShortUnsigned(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.getShort(index) & 0xffff;
  }

  @Override
  public void convert(LiteByteBuffer source, LiteByteBuffer target) {
    value = getInt(source);
    value = GrayCode.shortToGrayCode((short) value);
    putData(target);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.putShort((short) value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Range range() {
    return new Range(0, 65535);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }
}
