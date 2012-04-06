package rlpark.plugin.robot.internal.disco.drops;


import rlpark.plugin.rltoys.math.GrayCode;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.internal.disco.datatype.ScalarReader;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropShortSigned extends DropData implements ScalarReader, GrayCodeConverter {
  private short value;

  public DropShortSigned(String label) {
    this(label, -1);
  }

  public DropShortSigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropShortSigned(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.getShort(index);
  }

  @Override
  public void convert(LiteByteBuffer source, LiteByteBuffer target) {
    value = (short) getInt(source);
    value = GrayCode.shortToGrayCode(value);
    putData(target);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.putShort(value);
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Range range() {
    return new Range(-32768, 32767);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }
}
