package rlpark.plugin.robot.internal.sync;

import java.nio.ByteOrder;
import java.util.Arrays;

public class LiteByteBuffer {
  private final byte[] array;
  private final ByteOrder order;
  private int offset;

  public LiteByteBuffer(int dataSize) {
    this(dataSize, ByteOrder.BIG_ENDIAN);
  }

  public LiteByteBuffer(int capacity, ByteOrder order) {
    assert capacity > 0;
    this.order = order;
    array = new byte[capacity];
  }

  public LiteByteBuffer(byte[] buffer, ByteOrder order) {
    this.order = order;
    array = buffer.clone();
  }

  public ByteOrder order() {
    return order;
  }

  public byte[] array() {
    return array;
  }

  public int capacity() {
    return array.length;
  }

  public void clear() {
    reset();
    Arrays.fill(array, (byte) 0);
  }

  public void reset() {
    offset = 0;
  }

  public byte get() {
    return get(getMoveOffset(1));
  }

  public byte get(int index) {
    return array[index];
  }

  private int getMoveOffset(int shift) {
    int result = offset;
    offset += shift;
    assert offset <= array.length;
    return result;
  }

  public float getFloat() {
    return getFloat(getMoveOffset(4));
  }

  public float getFloat(int i) {
    return Float.intBitsToFloat(getBits(i, 4));
  }

  public int getInt(int i) {
    return getBits(i, 4);
  }

  public short getShort() {
    return getShort(getMoveOffset(2));
  }

  public short getShort(int index) {
    return (short) getBits(index, 2);
  }

  private int getBits(int offset, int length) {
    assert length > 0 && length <= 8;
    if (order == ByteOrder.BIG_ENDIAN)
      return getBitsBigEndian(offset, length);
    return getBitsSmallEndian(offset, length);
  }

  public int getBitsBigEndian(int offset, int length) {
    int result = 0;
    for (int i = 0; i < length; i++) {
      result = result | (0x000000ff & array[offset + i]);
      if (length - i > 1)
        result = result << 8;
    }
    return result;
  }

  public int getBitsSmallEndian(int offset, int length) {
    int result = 0;
    for (int i = 0; i < length; i++) {
      result = result | (0x000000ff & array[offset + length - i - 1]);
      if (length - i > 1)
        result = result << 8;
    }
    return result;
  }

  public int getInt() {
    return getInt(getMoveOffset(4));
  }

  public void put(byte value) {
    array[getMoveOffset(1)] = value;
  }

  public void put(byte[] values) {
    System.arraycopy(values, 0, array, getMoveOffset(values.length), values.length);
  }

  public void putFloat(float value) {
    putBits(Float.floatToIntBits(value), 4);
  }

  public void putShort(short value) {
    putBits(value, 2);
  }

  public void putInt(int value) {
    putBits(value, 4);
  }

  private void putBits(int value, int length) {
    assert length > 0 && length <= 8;
    int index = getMoveOffset(length);
    if (order == ByteOrder.BIG_ENDIAN)
      putBitsBigEndian(index, value, length);
    else
      putBitsLittleEndian(index, value, length);
  }

  private void putBitsBigEndian(int index, int value, int length) {
    int buffer = value;
    for (int i = 0; i < length; i++) {
      array[index + length - i - 1] = (byte) (buffer & 0xFF);
      buffer = buffer >> 8;
    }
  }

  private void putBitsLittleEndian(int index, int value, int length) {
    int buffer = value;
    for (int i = 0; i < length; i++) {
      array[index + i] = (byte) (buffer & 0xFF);
      buffer = buffer >> 8;
    }
  }

  public int offset() {
    return offset;
  }
}
