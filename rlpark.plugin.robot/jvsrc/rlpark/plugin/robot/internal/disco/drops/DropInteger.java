package rlpark.plugin.robot.internal.disco.drops;


import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datatype.ScalarReader;
import rlpark.plugin.robot.internal.disco.datatype.ScalarWriter;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropInteger extends DropData implements ScalarReader, ScalarWriter {
  protected int value;

  public DropInteger(String label) {
    this(label, false, -1);
  }

  public DropInteger(String label, int value) {
    this(label, value, -1);
  }

  public DropInteger(String label, int value, int index) {
    this(label, true, index);
    this.value = value;
  }

  protected DropInteger(String label, boolean readOnly, int index) {
    super(label, readOnly, index);
  }

  @Override
  public void setDouble(double value) {
    assert !readOnly;
    this.value = (int) value;
  }

  @Override
  public DropData clone(String label, int index) {
    if (readOnly)
      return new DropInteger(label, value, index);
    return new DropInteger(label, false, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.getInt(index);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.putInt(value);
  }

  @Override
  public int size() {
    return IntSize;
  }

  @Override
  public Range range() {
    return new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }
}
