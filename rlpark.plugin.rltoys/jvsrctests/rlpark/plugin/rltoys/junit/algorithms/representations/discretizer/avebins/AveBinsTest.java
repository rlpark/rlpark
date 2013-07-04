package rlpark.plugin.rltoys.junit.algorithms.representations.discretizer.avebins;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.avebins.AveBins;
import rlpark.plugin.rltoys.math.normalization.IncMeanVarNormalizer;

public class AveBinsTest {
  @Test
  public void testBins() {
    testBins(2);
    testBins(8);
    testBins(10);
  }

  private void testBins(int nbBins) {
    AveBins avebins = new AveBins(new IncMeanVarNormalizer(), nbBins);
    Random random = new Random(0);
    for (int i = 0; i < 10000; i++)
      avebins.discretize(random.nextFloat() * avebins.resolution());
    for (int i = 0; i < avebins.resolution(); i++)
      Assert.assertEquals(i, avebins.discretize(i));
  }

  @Test
  public void testIsolatedValueBins() {
    int[] values = new int[] { 1, 2, 6, 7 };
    AveBins avebins = new AveBins(new IncMeanVarNormalizer(), 10);
    Random random = new Random(0);
    for (int i = 0; i < 10000; i++)
      avebins.discretize(values[random.nextInt(values.length)]);
    Set<Integer> bins = new HashSet<Integer>();
    for (int i = 0; i < values.length; i++)
      bins.add(avebins.discretize(values[i]));
    Assert.assertEquals(values.length, bins.size());
  }
}
