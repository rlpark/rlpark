package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;

public class TileCodersUniformityTest {
  static private int NbSamplesPerWidth = 50000;
  static private Range Range = new Range(-2, 45);

  @Test
  public void testUniformity01() {
    testUniformity(new Random(0), Range, new TileCodersNoHashing(new WrappedPartitionFactory(Range), 1), 9, 2);
    testUniformity(new Random(0), Range, new TileCodersNoHashing(new WrappedPartitionFactory(Range), 1), 9, 9);
  }

  private void testUniformity(Random random, Range range, TileCoders coders, int gridResolution, int nbTilings) {
    coders.addFullTilings(gridResolution, nbTilings);
    PVector stats = new PVector(coders.vectorSize());
    int nbSamples = NbSamplesPerWidth * coders.vectorSize();
    for (int n = 0; n < nbSamples; n++)
      stats.addToSelf(coders.project(new double[] { range.choose(random) }));
    Assert.assertEquals(nbSamples * nbTilings, ((int) Vectors.l1Norm(stats)));
    checkStats(nbSamples * nbTilings / (double) coders.vectorSize(), stats);
  }

  private void checkStats(double expected, PVector stats) {
    double farthest = 0;
    for (double stat : stats.data)
      farthest = Math.max(farthest, Math.abs(stat - expected));
    Assert.assertTrue(String.valueOf(farthest / expected), (farthest / expected) * 100 < 1);
  }
}
