package rlpark.plugin.rltoys.junit.algorithms.representations;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.observations.ObsHistory;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.junit.utils.RLToysTestUtils;
import rlpark.plugin.rltoys.math.ranges.Range;

public class ObsHistoryTest {
  @Test
  public void testObservationLegend() {
    ObsHistory obsHistory = new ObsHistory(2, new Legend("a", "b"));
    String[] expectedLabels = new String[] { "a[t-2]", "b[t-2]", "a[t-1]", "b[t-1]", "a[t-0]", "b[t-0]" };
    for (int i = 0; i < expectedLabels.length; i++)
      Assert.assertEquals(i, obsHistory.legend().indexOf(expectedLabels[i]));
  }

  @Test
  public void testObservationRanges() {
    Range[] ranges = new Range[] { new Range(0, 1), new Range(0, 2) };
    ObsHistory obsHistory = new ObsHistory(2, new Legend("a", "b"), ranges);
    Range[] expectedRanges = new Range[] { ranges[0], ranges[1], ranges[0], ranges[1], ranges[0], ranges[1] };
    Assert.assertArrayEquals(expectedRanges, obsHistory.getRanges());
  }

  @Test
  public void testLabelSelection() {
    ObsHistory obsHistory = new ObsHistory(2, new Legend("a", "b"));
    Assert.assertEquals(0, obsHistory.selectIndexes(2, "a")[0]);
    Assert.assertEquals(1, obsHistory.selectIndexes(2, "b")[0]);
    Assert.assertEquals(4, obsHistory.selectIndexes(0, "a")[0]);
    int[] selected = obsHistory.selectIndexes(0, "a", "b");
    Assert.assertEquals(4, selected[0]);
    Assert.assertEquals(5, selected[1]);
  }

  @Test
  public void testObservationShift() {
    ObsHistory obsHistory = new ObsHistory(2, new Legend("a", "b"));
    RLToysTestUtils.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 2.0 },
                            obsHistory.update(new double[] { 1.0, 2.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 0.0, 0.0, 1.0, 2.0, 3.0, 4.0 },
                            obsHistory.update(new double[] { 3.0, 4.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 },
                            obsHistory.update(new double[] { 5.0, 6.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 },
                            obsHistory.update(new double[] { 7.0, 8.0 }), 0);
  }

  @Test
  public void testObservationShiftWithNoHistory() {
    ObsHistory obsHistory = new ObsHistory(0, new Legend("a", "b"));
    RLToysTestUtils.assertArrayEquals(new double[] { 1.0, 2.0 }, obsHistory.update(new double[] { 1.0, 2.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 3.0, 4.0 }, obsHistory.update(new double[] { 3.0, 4.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 5.0, 6.0 }, obsHistory.update(new double[] { 5.0, 6.0 }), 0);
    RLToysTestUtils.assertArrayEquals(new double[] { 7.0, 8.0 }, obsHistory.update(new double[] { 7.0, 8.0 }), 0);
    String[] expectedLabels = new String[] { "a[t-0]", "b[t-0]" };
    for (int i = 0; i < expectedLabels.length; i++)
      Assert.assertEquals(i, obsHistory.legend().indexOf(expectedLabels[i]));
  }
}
