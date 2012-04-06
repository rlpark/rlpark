package rlpark.plugin.robot;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.robot.helpers.Robots;

public class RobotsTest {
  @Test
  public void testDoubleArrayToByteArray() {
    int numTests = 100;
    int numDim = 54;
    for (int i = 0; i < numTests; i++) {
      double[] num = new double[numDim];
      for (int j = 0; j < numDim; j++)
        num[j] = (int) (Math.random() * 256 - 128);
      byte[] ba = Robots.doubleArrayToByteArray(num);
      double[] newNum = Robots.byteArrayToDoubleArray(ba);
      Assert.assertArrayEquals(num, newNum, 0.0);
    }
  }
}
