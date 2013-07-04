package rlpark.plugin.rltoys.junit.math;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.math.GrayCode;

public class GrayCodeTest {
  interface GrayCodeConverter {
    int toGrayCode(int value);
  }

  static private final GrayCodeConverter byteGrayCode = new GrayCodeConverter() {
    @Override
    public int toGrayCode(int value) {
      return GrayCode.byteToGrayCode((byte) value);
    }
  };
  static private final GrayCodeConverter shortGrayCode = new GrayCodeConverter() {
    @Override
    public int toGrayCode(int value) {
      return GrayCode.shortToGrayCode((short) value);
    }
  };
  static private final GrayCodeConverter intGrayCode = new GrayCodeConverter() {
    @Override
    public int toGrayCode(int value) {
      return GrayCode.intToGrayCode(value);
    }
  };

  protected void checkConverter(GrayCodeConverter converter) {
    Assert.assertEquals(4, converter.toGrayCode(7));
    Assert.assertEquals(7, converter.toGrayCode(5));
  }

  @Test
  public void testGrayCode() {
    checkConverter(byteGrayCode);
    checkConverter(shortGrayCode);
    checkConverter(intGrayCode);
  }

  @Test
  public void testCountValues() {
    checkConverterSpan(byteGrayCode, Byte.MIN_VALUE, Byte.MAX_VALUE, 256);
    checkConverterSpan(shortGrayCode, Byte.MIN_VALUE, Byte.MAX_VALUE, 256);
    checkConverterSpan(intGrayCode, Byte.MIN_VALUE, Byte.MAX_VALUE, 256);

    checkConverterSpan(byteGrayCode, Short.MIN_VALUE, Short.MAX_VALUE, 256);
    checkConverterSpan(shortGrayCode, Short.MIN_VALUE, Short.MAX_VALUE, 65536);
    checkConverterSpan(intGrayCode, Short.MIN_VALUE, Short.MAX_VALUE, 65536);

    checkConverterSpan(byteGrayCode, Short.MIN_VALUE, Short.MAX_VALUE, 256);
    checkConverterSpan(shortGrayCode, Short.MIN_VALUE, Short.MAX_VALUE, 65536);
    checkConverterSpan(intGrayCode, Short.MIN_VALUE - 10, Short.MAX_VALUE + 10, 65536 + 20);
  }

  private void checkConverterSpan(GrayCodeConverter converter, int minValue, int maxValue, int expected) {
    Set<Integer> values = new HashSet<Integer>();
    for (int i = minValue; i <= maxValue; i++)
      values.add(converter.toGrayCode(i));
    Assert.assertEquals(expected, values.size());
  }

}
