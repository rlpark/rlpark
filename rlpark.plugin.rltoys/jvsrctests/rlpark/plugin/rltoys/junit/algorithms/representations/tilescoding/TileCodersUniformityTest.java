package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedBigPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedSmallPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;

public class TileCodersUniformityTest {
  static private int NbSamplesPerWidth = 20000;
  static private Range Range = new Range(-2, 45);

  @Test
  public void testUniformity01() {
    testUniformity(new Random(0), Range, new TileCodersNoHashing(new WrappedPartitionFactory(Range), 1), 9, 2);
    testUniformity(new Random(0), Range, new TileCodersNoHashing(new WrappedPartitionFactory(Range), 1), 9, 9);
  }

  private void testUniformity(Random random, Range range, TileCoders coders, int gridResolution, int nbTilings) {
    coders.addFullTilings(gridResolution, nbTilings);
    int nbSamples = NbSamplesPerWidth * coders.vectorSize();
    PVector stats = computeStats(random, range, coders, gridResolution, nbTilings, nbSamples);
    checkStats(nbSamples * nbTilings / (double) coders.vectorSize(), stats);
  }

  static private PVector computeStats(Random random, Range range, TileCoders coders, int gridResolution, int nbTilings,
      int nbSamples) {
    PVector stats = new PVector(coders.vectorSize());
    for (int n = 0; n < nbSamples; n++)
      stats.addToSelf(coders.project(new double[] { range.choose(random) }));
    Assert.assertEquals(nbSamples * nbTilings, ((int) Vectors.l1Norm(stats)));
    return stats;
  }

  private void checkStats(double expected, PVector stats) {
    double farthest = 0;
    for (double stat : stats.data)
      farthest = Math.max(farthest, Math.abs(stat - expected));
    Assert.assertTrue(String.valueOf(farthest / expected), (farthest / expected) * 100 < 1);
  }

  private static void projectSamples(Range range, DiscretizerFactory partitionFactory, int nbSamples) {
    int gridResolution = 5;
    int nbTilings = 5;
    Random random = new Random(0);
    System.out.println("^^^^" + partitionFactory.getClass().getSimpleName() + "^^^^");
    TileCodersNoHashing coders = new TileCodersNoHashing(partitionFactory, 1);
    coders.addFullTilings(gridResolution, nbTilings);
    System.out.println(coders.toString());
    PVector stats = computeStats(random, range, coders, gridResolution, nbTilings, nbSamples);
    System.out.println("Nb On: " + stats.toString());
    int expectedAverage = nbSamples / coders.vectorSize() * nbTilings;
    for (int i = 0; i < stats.data.length; i++)
      stats.data[i] = stats.data[i] / expectedAverage;
    System.out.println("Nb On (relative to average): " + stats.toString());
    StringBuilder info = new StringBuilder();
    double min = Double.MAX_VALUE, max = 0;
    double stddev = 0;
    for (int i = 0; i < stats.data.length; i++) {
      double value = stats.data[i];
      min = Math.min(min, value);
      max = Math.max(max, value);
      stddev += (value - 1) * (value - 1);
      info.append((int) Math.round((value) * 100) + "%, ");
    }
    stddev = Math.sqrt(stddev / stats.data.length);
    System.out.println(String.format("[%.2f,%.2f] range=%.2f stddev=%.2f", min, max, max - min, stddev));
    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
  }

  public static void main(String[] args) {
    int nbSamples = 10000000;
    Range range = new Range(0, 1);
    projectSamples(range, new WrappedPartitionFactory(range), nbSamples);
    projectSamples(range, new BoundedBigPartitionFactory(range), nbSamples);
    projectSamples(range, new BoundedSmallPartitionFactory(range), nbSamples);
  }
}
