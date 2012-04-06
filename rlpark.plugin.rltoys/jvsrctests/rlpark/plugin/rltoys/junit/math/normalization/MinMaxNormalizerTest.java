package rlpark.plugin.rltoys.junit.math.normalization;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.Constants;
import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;


public class MinMaxNormalizerTest {
  @Test
  public void testMinMaxNormalizer() {
    MinMaxNormalizer normalizer = new MinMaxNormalizer();
    Assert.assertEquals(0.0, normalizer.normalize(0.5), 0.0);
    normalizer.update(0);
    Assert.assertEquals(0.0, normalizer.normalize(0.5), 0.0);
    normalizer.update(1);
    Assert.assertEquals(0.0, normalizer.normalize(0.5), Constants.EPSILON);
    normalizer.update(2);
    Assert.assertEquals(-0.5, normalizer.normalize(0.5), Constants.EPSILON);
  }

  @Test
  public void testMinMaxNormalizerWithRange() {
    MinMaxNormalizer normalizer = new MinMaxNormalizer(new Range(-2, -1));
    normalizer.update(10);
    normalizer.update(20);
    Assert.assertEquals(-2.0, normalizer.normalize(10), 0.0);
    Assert.assertEquals(-1.0, normalizer.normalize(20), 0.0);
    Assert.assertEquals(-1.5, normalizer.normalize(15), Constants.EPSILON);
  }
}
