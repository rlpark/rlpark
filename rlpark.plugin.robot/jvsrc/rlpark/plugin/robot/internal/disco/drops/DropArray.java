package rlpark.plugin.robot.internal.disco.drops;


import java.util.Arrays;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;


public class DropArray extends DropData {
  final private DropData[] dropDatas;
  private final int size;
  private final String[] suffixes;
  private final DropData prototype;

  public DropArray(String label, int nbData) {
    this(label, getSuffixes(nbData));
  }

  public DropArray(DropData prototype, String label, int nbData) {
    this(prototype, label, -1, getSuffixes(nbData));
  }

  public DropArray(String label, String... suffixes) {
    this(new DropInteger(""), label, -1, suffixes);
  }

  public DropArray(DropData prototype, String label, String... suffixes) {
    this(prototype, label, -1, suffixes);
  }

  public DropArray(DropData prototype, String label, int index, String... suffixes) {
    super(label, false, index);
    assert suffixes.length > 0;
    assert prototype != null;
    this.prototype = prototype;
    dropDatas = new DropData[suffixes.length];
    int sum = 0;
    for (int i = 0; i < suffixes.length; i++) {
      int byteIndex = index != -1 ? sum + index : -1;
      DropData data = prototype.clone(label + suffixes[i], byteIndex);
      dropDatas[i] = data;
      sum += data.size();
    }
    size = sum;
    this.suffixes = suffixes;
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropArray(prototype, label, index, suffixes);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    for (DropData data : dropDatas)
      data.putData(buffer);
  }

  @Override
  public int size() {
    return size;
  }

  private static String[] getSuffixes(int nbData) {
    String[] suffixes = new String[nbData];
    for (int i = 0; i < suffixes.length; i++)
      suffixes[i] = String.format("%d", i);
    return suffixes;
  }

  @Override
  public String toString() {
    String[] values = new String[dropDatas.length];
    for (int i = 0; i < values.length; i++)
      values[i] = dropDatas[i].toString();
    return label + ": " + Arrays.toString(values);
  }

  public DropData[] dropDatas() {
    return dropDatas;
  }
}
