package rlpark.plugin.rltoys.junit.math.ranges;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.math.ranges.Range;

public class RangeTest {
  @Test
  public void testRangeIn() {
    Range range = new Range(-2.0, -1.0);
    Assert.assertFalse(range.in(-0.5));
    Assert.assertTrue(range.in(-1.5));
  }

  @Test
  public void testRangeBound() {
    Range range = new Range(-2.0, -1.0);
    Assert.assertTrue(range.in(range.bound(-1.5)));
    Assert.assertTrue(range.in(range.bound(-3.0)));
    Assert.assertTrue(range.in(range.bound(0.0)));
  }

  @Test
  public void testRangeChoose() {
    Range range = new Range(-2.0, -1.0);
    Random random = new Random(0);
    for (int i = 0; i < 100; i++) {
      double value = range.choose(random);
      Assert.assertTrue(value < range.max());
      Assert.assertTrue(value >= range.min());
    }
  }

  @Test
  public void testRangeSample() {
    Range range = new Range(1.0, 2.0);
    double[] sampled = range.sample(100);
    Set<Double> values = new LinkedHashSet<Double>();
    for (double value : sampled) {
      Assert.assertTrue(value < range.max());
      Assert.assertTrue(value >= range.min());
      values.add(value);
    }
    Assert.assertEquals(values.size(), 100);
  }
}
