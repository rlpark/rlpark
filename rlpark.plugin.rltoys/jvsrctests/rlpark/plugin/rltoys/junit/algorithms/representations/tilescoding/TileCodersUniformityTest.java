package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.BinaryVector;

public class TileCodersUniformityTest {
  static private int NbSamplesPerWidth = 50000;
  static private Range range = new Range(-2, 45);

  @Test
  public void testUniformity01() {
    testUniformity(new Random(0), range, new TileCodersNoHashing(new WrappedPartitionFactory(range), 1), 9, 2);
  }

  @Test
  public void testUniformity02() {
    testUniformity(new Random(0), range, new TileCodersNoHashing(range), 2, 4);
    testUniformity(new Random(0), range, new TileCodersNoHashing(range), 2, 9);
  }

  private void testUniformity(Random random, Range range, TileCoders coders, int gridResolution, int nbTilings) {
    coders.addFullTilings(gridResolution, nbTilings);
    int[] stats = new int[gridResolution];
    int nbSamples = NbSamplesPerWidth * gridResolution;
    for (int n = 0; n < nbSamples; n++) {
      BinaryVector x = coders.project(new double[] { range.choose(random) });
      for (int i : x.getActiveIndexes())
        stats[i % gridResolution] += 1;
    }
    checkStats(nbSamples / (double) gridResolution * nbTilings, stats);
  }

  private void checkStats(double expected, int[] stats) {
    double farthest = 0;
    for (int stat : stats)
      farthest = Math.max(farthest, Math.abs(stat - expected));
    Assert.assertTrue(String.valueOf(farthest / expected), (farthest / expected) * 100 < 1);
  }
}
