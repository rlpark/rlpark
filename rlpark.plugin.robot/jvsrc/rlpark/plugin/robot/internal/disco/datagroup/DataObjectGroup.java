package rlpark.plugin.robot.internal.disco.datagroup;

import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public abstract class DataObjectGroup<T> extends DataGroup {
  public DataObjectGroup(Drop drop) {
    this("", drop);
  }

  public DataObjectGroup(String prefix, Drop drop) {
    super(prefix, drop);
  }

  protected abstract T getValue(LiteByteBuffer byteBuffer, DropData dropData);

  public void set(T... values) {
    assert values.length == dropDatas.length;
    for (int i = 0; i < values.length; i++)
      setValue(dropDatas[i], values[i]);
  }

  public void get(LiteByteBuffer byteBuffer, T[] values) {
    assert values.length == dropDatas.length;
    for (int i = 0; i < values.length; i++)
      values[i] = getValue(byteBuffer, dropDatas[i]);
  }

  abstract protected void setValue(DropData dropData, T value);
}
