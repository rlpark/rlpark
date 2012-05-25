package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.Assert;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;

public class VectorsTestsUtils {
  public static void assertEquals(RealVector a, RealVector b) {
    assertEquals(a, b, Float.MIN_VALUE);
  }

  public static void assertEquals(RealVector a, RealVector b, double margin) {
    Assert.assertTrue(equals(a, b, margin));
    Assert.assertArrayEquals(a.accessData(), b.accessData(), margin);
  }

  public static boolean equals(RealVector a, RealVector b) {
    return equals(a, b, 0);
  }

  public static boolean equals(RealVector a, RealVector b, double margin) {
    Assert.assertTrue(checkConsistency(a));
    Assert.assertTrue(checkConsistency(b));
    return Vectors.equals(a, b, margin);
  }

  static public boolean checkConsistency(RealVector v) {
    if (v == null)
      return true;
    if (v instanceof SVector && !checkSVectorConsistency((SVector) v))
      return false;
    return true;
  }

  private static boolean checkSVectorConsistency(SVector v) {
    int[] indexPositions = v.indexesPosition;
    int nbActiveCounted = 0;
    boolean[] positionChecked = new boolean[v.nonZeroElements()];
    for (int index = 0; index < indexPositions.length; index++) {
      final int position = indexPositions[index];
      if (position == -1)
        continue;
      if (nbActiveCounted >= positionChecked.length)
        return false;
      if (positionChecked[position])
        return false;
      if (v.activeIndexes[position] != index)
        return false;
      positionChecked[position] = true;
      nbActiveCounted++;
    }
    if (nbActiveCounted != v.nonZeroElements())
      return false;
    return true;
  }
}
