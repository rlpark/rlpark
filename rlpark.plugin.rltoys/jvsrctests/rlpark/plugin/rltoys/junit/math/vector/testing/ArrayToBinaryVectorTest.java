package rlpark.plugin.rltoys.junit.math.vector.testing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.utils.Utils;

public class ArrayToBinaryVectorTest {
  @Test
  public void testDoubleToBinary() {
    double[] o = new double[] { 0.0, 1.0, 2.0, 4.0, 8.0 };
    BVector bob = BVector.toBinary(o);
    int[] expecteds = new int[] { 32, 65, 98, 131 };
    Assert.assertArrayEquals(expecteds, bob.getActiveIndexes());
  }

  @Test
  public void testRandomIntToBinary() {
    Random random = new Random(0);
    int[] expected = new int[1000];
    for (int i = 0; i < expected.length; i++)
      expected[i] = random.nextInt();
    BVector bob = BVector.toBinary(expected);
    Assert.assertArrayEquals(expected, binaryToInts(bob));
  }

  private int[] binaryToInts(BVector bob) {
    List<Integer> intNumbers = new ArrayList<Integer>();
    BigInteger value = BigInteger.ZERO;
    for (int i : bob.getActiveIndexes()) {
      int nextIntegerBit = (intNumbers.size() + 1) * Integer.SIZE;
      while (i >= nextIntegerBit) {
        intNumbers.add(value.intValue());
        nextIntegerBit = (intNumbers.size() + 1) * Integer.SIZE;
        value = BigInteger.ZERO;
      }
      int bitIndex = i - nextIntegerBit + Integer.SIZE;
      value = value.setBit(bitIndex);
    }
    intNumbers.add(value.intValue());
    return Utils.asIntArray(intNumbers);
  }

  protected int toInteger(BitSet bitSet) {
    BigInteger value = BigInteger.ZERO;
    for (int j = 0; j < bitSet.size(); j++) {
      if (bitSet.get(j))
        value = value.setBit(j);
    }
    return value.intValue();
  }

  @Test
  public void testRandomByteToBinary() {
    Random random = new Random(0);
    byte[] expected = new byte[1000];
    random.nextBytes(expected);
    BVector bob = BVector.toBinary(byteArraytoFilledByteArray(expected));
    int[] ints = binaryToInts(bob);
    Assert.assertArrayEquals(expected, intArraytoByte(ints));
  }

  private byte[] byteArraytoFilledByteArray(byte[] bytes) {
    byte[] result = new byte[bytes.length * 4];
    for (int i = 0; i < bytes.length; i++)
      result[i * 4] = bytes[i];
    return result;
  }

  private byte[] intArraytoByte(int[] ints) {
    byte[] result = new byte[ints.length];
    for (int i = 0; i < result.length; i++)
      result[i] = (byte) ints[i];
    return result;
  }
}
