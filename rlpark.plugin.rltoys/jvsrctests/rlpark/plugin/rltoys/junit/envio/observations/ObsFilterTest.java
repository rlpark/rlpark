package rlpark.plugin.rltoys.junit.envio.observations;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsActionFilter;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import rlpark.plugin.rltoys.junit.utils.RLToysTestUtils;


public class ObsFilterTest {
  private final Legend legend = new Legend("a1", "a2", "a3", "b1", "b2", "b3");

  @Test
  public void testFilter() {
    Object[] labels = new ObsFilter(legend, "a").legend().getLabels().toArray();
    String[] expectedLabels = { "a1", "a2", "a3" };
    Assert.assertArrayEquals(expectedLabels, labels);

    labels = new ObsFilter(legend, "a1", "b2").legend().getLabels().toArray();
    expectedLabels = new String[] { "a1", "b2" };
    Assert.assertArrayEquals(expectedLabels, labels);
  }

  @Test
  public void testFilterExcluded() {
    Object[] labels = new ObsFilter(legend, true, "a").legend().getLabels().toArray();
    String[] expectedLabels = { "b1", "b2", "b3" };
    Assert.assertArrayEquals(expectedLabels, labels);

    labels = new ObsFilter(legend, true, "a1", "b2").legend().getLabels().toArray();
    expectedLabels = new String[] { "a2", "a3", "b1", "b3" };
    Assert.assertArrayEquals(expectedLabels, labels);
  }

  @Test
  public void testActionFilterExcluded() {
    ObsActionFilter filter = new ObsActionFilter(legend, 2, "a");
    Object[] labels = filter.legend().getLabels().toArray();
    String[] expectedLabels = { "a1", "a2", "a3", ObsActionFilter.Action + "0", ObsActionFilter.Action + "1" };
    Assert.assertArrayEquals(expectedLabels, labels);

    double[] o = filter.update(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 }, new ActionArray(-1.0, -2.0));
    RLToysTestUtils.assertArrayEquals(new double[] { 1.0, 2.0, 3.0, -1.0, -2.0 }, o, 0.0);
  }
}
