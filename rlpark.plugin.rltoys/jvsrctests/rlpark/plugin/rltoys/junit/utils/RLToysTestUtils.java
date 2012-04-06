package rlpark.plugin.rltoys.junit.utils;

import org.junit.Assert;

public class RLToysTestUtils {

  static public void assertArrayEquals(double[] expecteds, double[] actuals, double delta) {
    Assert.assertEquals(expecteds.length, actuals.length);
    for (int i = 0; i < actuals.length; i++)
      Assert.assertEquals(expecteds[i], actuals[i], delta);
  }

}
