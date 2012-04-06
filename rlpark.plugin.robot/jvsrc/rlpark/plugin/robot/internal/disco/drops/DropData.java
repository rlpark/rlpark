package rlpark.plugin.robot.internal.disco.drops;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;



public abstract class DropData {
  public static final int FloatSize = Float.SIZE / 8;
  public static final int IntSize = Integer.SIZE / 8;
  public static final int CharSize = Byte.SIZE / 8;
  public static final int ByteSize = Byte.SIZE / 8;

  final public String label;
  final public boolean readOnly;
  protected final int index;

  protected DropData(String label, boolean readOnly) {
    this(label, readOnly, -1);
  }


  protected DropData(String label, boolean readOnly, int index) {
    this.label = label;
    this.readOnly = readOnly;
    this.index = index;
  }

  public DropData clone(int index) {
    return clone(label, index);
  }

  abstract public DropData clone(String label, int index);

  abstract public int size();

  abstract public void putData(LiteByteBuffer buffer);
}
