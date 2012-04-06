package rlpark.plugin.robot.internal.disco.drops;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropByteArray extends DropData {
  private final byte[] value;

  public DropByteArray(String label, int length) {
    this(label, false, length, -1);
  }

  public DropByteArray(String label, boolean readonly, int length, int index) {
    super(label, readonly, index);
    value = new byte[length];
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropByteArray(label, readOnly, value.length, index);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    for (byte c : value)
      buffer.put(c);
  }

  @Override
  public int size() {
    return value.length * DropData.CharSize;
  }

  public byte[] value() {
    return value;
  }

  public void setValue(char[] chars) {
    setValue(toBytes(chars));
  }

  static public byte[] toBytes(char[] chars) {
    byte[] result = new byte[chars.length];
    for (int i = 0; i < result.length; i++)
      result[i] = (byte) chars[i];
    return result;
  }

  static public byte[] toBytes(int[] ints) {
    byte[] result = new byte[ints.length];
    for (int i = 0; i < result.length; i++)
      result[i] = (byte) ints[i];
    return result;
  }

  public void setValue(byte[] message) {
    System.arraycopy(message, 0, value, 0, value.length);
  }

  public byte[] getPascalStringValue() {
    byte[] result = new byte[value[0]];
    System.arraycopy(value, 1, result, 0, result.length);
    return result;
  }

  public void setPascalStringValue(char[] message) {
    setPascalStringValue(toBytes(message));
  }

  public void setPascalStringValue(byte[] message) {
    value[0] = (byte) message.length;
    System.arraycopy(message, 0, value, 1, message.length);
  }

}
