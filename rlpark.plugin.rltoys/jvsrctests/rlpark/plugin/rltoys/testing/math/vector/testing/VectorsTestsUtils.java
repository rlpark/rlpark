package rlpark.plugin.rltoys.testing.math.vector.testing;

import org.junit.Assert;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;


public class VectorsTestsUtils {

  public static void assertEquals(RealVector a, RealVector b) {
    Assert.assertTrue(Vectors.equals(a, b));
    Assert.assertArrayEquals(a.accessData(), b.accessData(), Float.MIN_VALUE);
  }

}
