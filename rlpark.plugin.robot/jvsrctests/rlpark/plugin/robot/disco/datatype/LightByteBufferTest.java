package rlpark.plugin.robot.disco.datatype;

import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class LightByteBufferTest {
  @Test
  public void testBytes() {
    testBytes(ByteOrder.LITTLE_ENDIAN);
    testBytes(ByteOrder.BIG_ENDIAN);
  }

  private void testBytes(ByteOrder order) {
    byte[] values = new byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE, 45, -47 };
    LiteByteBuffer buffer = new LiteByteBuffer(values.length, order);
    for (byte value : values)
      buffer.put(value);
    buffer.reset();
    for (byte value : values)
      Assert.assertEquals(value, buffer.get());
  }

  @Test
  public void testInts() {
    testInts(ByteOrder.LITTLE_ENDIAN);
    testInts(ByteOrder.BIG_ENDIAN);
  }

  private void testInts(ByteOrder order) {
    int[] values = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE, 45, -47, 25899, -58632147 };
    LiteByteBuffer buffer = new LiteByteBuffer(values.length * Integer.SIZE / 8, order);
    for (int value : values)
      buffer.putInt(value);
    buffer.reset();
    for (int value : values)
      Assert.assertEquals(value, buffer.getInt());
  }

  @Test
  public void testShorts() {
    testShorts(ByteOrder.LITTLE_ENDIAN);
    testShorts(ByteOrder.BIG_ENDIAN);
  }

  private void testShorts(ByteOrder order) {
    short[] values = new short[] { Short.MIN_VALUE, Short.MAX_VALUE, 45, -47, 25899, -5863 };
    LiteByteBuffer buffer = new LiteByteBuffer(values.length * Short.SIZE / 8, order);
    for (short value : values)
      buffer.putShort(value);
    buffer.reset();
    for (short value : values)
      Assert.assertEquals(value, buffer.getShort());
  }

  @Test
  public void testFloats() {
    testFloats(ByteOrder.LITTLE_ENDIAN);
    testFloats(ByteOrder.BIG_ENDIAN);
  }

  private void testFloats(ByteOrder order) {
    float[] values = new float[] { Float.MIN_VALUE, Float.MAX_VALUE, 45.3325845f, -47.2566f, 25899.2584f, -5863.5885f };
    LiteByteBuffer buffer = new LiteByteBuffer(values.length * Float.SIZE / 8, order);
    for (float value : values)
      buffer.putFloat(value);
    buffer.reset();
    for (float value : values)
      Assert.assertEquals(value, buffer.getFloat(), 0);
  }
}
