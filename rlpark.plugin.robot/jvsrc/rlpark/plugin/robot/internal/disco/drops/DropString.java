package rlpark.plugin.robot.internal.disco.drops;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;



public class DropString extends DropData {

  private String value;

  public DropString(String label) {
    this(label, "", false, 0);
  }

  public DropString(String label, String name) {
    this(label, name, true, -1);
  }

  public DropString(String label, String value, boolean readonly, int index) {
    super(label, readonly, index);
    this.value = value;
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropString(label, value, readOnly, index);
  }

  public boolean checkString(LiteByteBuffer buffer) {
    if (buffer.getInt(0) != value.length())
      return false;
    byte[] charName = new byte[value.length()];
    for (int i = 0; i < value.length(); i++)
      charName[i] = buffer.get(i + DropData.IntSize);
    return value.equals(new String(charName));
  }

  static public String getData(LiteByteBuffer buffer, int index) {
    final int stringSize = buffer.capacity();
    byte[] byteName = new byte[stringSize];
    for (int i = 0; i < stringSize; i++)
      byteName[i] = buffer.get(i);
    return new String(byteName);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.putInt(value.length());
    for (byte b : value.getBytes())
      buffer.put(b);
  }

  @Override
  public int size() {
    return value.length() * DropData.CharSize + DropData.IntSize;
  }

  public String value() {
    return value;
  }

  public void set(String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }
}
