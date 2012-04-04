package rlpark.plugin.rltoys.math;

public class GrayCode {
  static private final byte Base = 2;

  static public byte byteToGrayCode(byte value) {
    int positiveValue = value >= 0 ? value : Byte.MAX_VALUE - value;
    return (byte) toGrayCode(toBaseNArray(Byte.SIZE, positiveValue));
  }

  static public short shortToGrayCode(short value) {
    int positiveValue = value >= 0 ? value : Short.MAX_VALUE - value;
    return (short) toGrayCode(toBaseNArray(Short.SIZE, positiveValue));
  }

  static public int intToGrayCode(int value) {
    int positiveValue = value >= 0 ? value : (int) ((long) Integer.MAX_VALUE - value);
    return toGrayCode(toBaseNArray(Integer.SIZE, positiveValue));
  }

  /**
   * From http://en.wikipedia.org/wiki/Gray_code
   */
  static private int toGrayCode(int[] baseN) {
    int digits = baseN.length;
    int gray[] = new int[digits];
    int shift = 0;
    for (int i = digits - 1; i >= 0; i--) {
      gray[i] = (baseN[i] - shift) % Base;
      shift += gray[i] - Base;
    }
    return toInt(gray);
  }

  private static int toInt(int[] gray) {
    int value = 0;
    for (int i = 0; i < gray.length; i++)
      value = value | gray[i] << i;
    return value;
  }

  protected static int[] toBaseNArray(int digits, int value) {
    int[] baseN = new int[digits];
    int tempvalue = value;
    for (int i = 0; i < digits; i++) {
      baseN[i] = tempvalue % Base;
      tempvalue /= Base;
    }
    return baseN;
  }

  public static byte[] toGrayCode(byte[] rawData) {
    byte[] result = rawData.clone();
    for (int i = 0; i < rawData.length; i++)
      result[i] = byteToGrayCode(rawData[i]);
    return result;
  }
}
