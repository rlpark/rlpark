package critterbot.crtrlog;

import junit.framework.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.testing.math.vector.testing.VectorsTestsUtils;
import rlpark.plugin.rltoys.utils.Paths;

public class CrtrLogFileTest {

  static final double[][] inputExpected01 = { { 22000.0, 1.0, 2.0, 3.0, 4.0, 5.0 },
      { 22010.0, 2.0, 2.0, 3.0, 4.0, 5.0 }, { 22020.0, 1.0, 2.0, 3.0, 4.0, 5.0 }, { 22030.0, 2.0, 2.0, 3.0, 4.0, 5.0 },
      { 22040.0, 3.0, 2.0, 3.0, 4.0, 5.0 } };

  @Test
  public void testRanges() {
    Range[] ranges = LogFiles.extractRanges(Paths.getDataPath("rlpark.plugin.critterbot", "unittesting01.crtrlog"),
                                            false);
    Range[] expectedRanges = new Range[] { new Range(22000, 222040), new Range(1, 3), new Range(2, 2), new Range(3, 3),
        new Range(4, 4), new Range(5, 5) };
    Assert.assertEquals(expectedRanges.length, ranges.length);
    for (int i = 0; i < expectedRanges.length; i++)
      ranges[i].equals(expectedRanges[i]);
  }

  @Test
  public void testLogFileToFeatures() {
    CrtrLogFile logFile = new CrtrLogFile(Paths.getDataPath("rlpark.plugin.critterbot", "unittesting01.crtrlog"));
    compareWithExpected(logFile, inputExpected01);
  }

  static public void compareWithExpected(CrtrLogFile logFile, double[][] expected) {
    int timeIndex = 0;
    while (logFile.hasNextStep()) {
      double[] step = logFile.step();
      VectorsTestsUtils.assertEquals(new PVector(expected[timeIndex]), new PVector(step));
      timeIndex += 1;
    }
    Assert.assertEquals(timeIndex, expected.length);
  }
}
