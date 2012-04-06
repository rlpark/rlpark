package rlpark.plugin.robot.internal.disco.drops;


import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datatype.ScalarReader;
import rlpark.plugin.robot.internal.disco.datatype.ScalarWriter;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropFloat extends DropData implements ScalarReader, ScalarWriter {
  protected float value;

  public DropFloat(String label) {
    this(label, -1);
  }

  protected DropFloat(String label, int index) {
    super(label, false, index);
  }

  public DropFloat(String label, float value, int index) {
    super(label, true, index);
    this.value = value;
  }

  @Override
  public DropData clone(String label, int index) {
    if (readOnly)
      return new DropFloat(label, value, index);
    return new DropFloat(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return (int) getDouble(buffer);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.putFloat(value);
  }

  @Override
  public int size() {
    return FloatSize;
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return buffer.getFloat(index);
  }

  @Override
  public void setDouble(double value) {
    this.value = (float) value;
  }

  @Override
  public Range range() {
    return new Range(-Float.MAX_VALUE, Float.MAX_VALUE);
  }
}
