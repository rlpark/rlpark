package rlpark.plugin.robot.internal.disco.drops;


import java.util.Arrays;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;


public class Drop {
  private final DropData[] dropDatas;
  private final DropString dropName;
  private final DropTime dropTime;

  public Drop(String name, DropData... dropData) {
    assert name != null;
    dropName = new DropString("Name", name);
    dropDatas = new DropData[dropData.length];
    DropData dropTime = null;
    int byteIndex = 0;
    for (int i = 0; i < dropData.length; i++) {
      DropData cloned = dropData[i].clone(byteIndex);
      dropDatas[i] = cloned;
      if (cloned.label.equals(DropTime.TIMELABEL)) {
        assert dropTime == null;
        dropTime = cloned;
      }
      byteIndex += cloned.size();
    }
    this.dropTime = (DropTime) dropTime;
  }

  public int dataSize() {
    int sum = 0;
    for (DropData data : dropDatas)
      sum += data.size();
    return sum;
  }

  public void putData(LiteByteBuffer buffer) {
    dropName.putData(buffer);
    buffer.putInt(dataSize());
    for (DropData data : dropDatas)
      data.putData(buffer);
  }

  @Override
  public String toString() {
    String[] values = new String[dropDatas.length];
    for (int i = 0; i < values.length; i++)
      values[i] = dropDatas[i].toString();
    return dropName.value() + ": " + Arrays.toString(values);
  }

  public String name() {
    return dropName.value();
  }

  public long time() {
    return dropTime.time();
  }

  public DropData[] dropDatas() {
    return dropDatas;
  }

  public void setTime(long time) {
    dropTime.set(time);
  }

  public DropData drop(String label) {
    for (DropData drop : dropDatas)
      if (drop.label.equals(label))
        return drop;
    return null;
  }

  public int headerSize() {
    return dropName.size() + DropData.IntSize;
  }

  public int packetSize() {
    return headerSize() + dataSize();
  }

  public int indexOf(String label) {
    int index = 0;
    for (DropData dropData : dropDatas) {
      if (dropData.label.equals(label))
        return index;
      index += dropData.size();
    }
    return index;
  }
}
