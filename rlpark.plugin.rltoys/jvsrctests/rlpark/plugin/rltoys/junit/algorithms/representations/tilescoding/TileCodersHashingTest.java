package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import static rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders.buildRanges;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.ColisionDetection;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.JavaHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding.TileCodersNoHashingTest.TileCodersFactory;
import rlpark.plugin.rltoys.math.ranges.Range;

public class TileCodersHashingTest {
  static int memorySize = 100000;

  @Test
  public void testMemorySize() {
    TileCoders tileCoders = new TileCodersHashing(new UNH(new Random(0), memorySize), new Range(0, 1), new Range(0, 1));
    tileCoders.addFullTilings(2, 1);
    Assert.assertEquals(memorySize, tileCoders.vectorSize());
    tileCoders.addFullTilings(2, 1);
    Assert.assertEquals(memorySize, tileCoders.vectorSize());
  }

  @Test
  public void testUNHHashingActivationFrequency() {
    TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        return new TileCodersHashing(new UNH(new Random(0), memorySize * 10), nbInputs, 0, 1);
      }
    });
  }

  @Test
  public void testUNHHashingActivationFrequencyWithRandom() {
    TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        AbstractPartitionFactory discretizerFactory = new WrappedPartitionFactory(buildRanges(nbInputs, min, max));
        discretizerFactory.setRandom(new Random(0), 0.1);
        TileCoders coders = new TileCodersHashing(new UNH(new Random(0), memorySize), discretizerFactory, nbInputs);
        return coders;
      }
    });
  }

  @Test
  public void testCollisionCounting() {
    final ColisionDetection hashing = new ColisionDetection(new UNH(new Random(0), 2));
    TileCoders tileCoders = new TileCodersHashing(hashing, new WrappedPartitionFactory(buildRanges(2, 0, 1)), 2);
    tileCoders.addFullTilings(2, 1);
    int nbSamples = 10000;
    Random random = new Random(0);
    for (int i = 0; i < nbSamples; i++)
      tileCoders.project(new double[] { random.nextDouble(), random.nextDouble() });
    Assert.assertEquals((double) nbSamples / 2, hashing.nbCollisions(), 1000);
  }

  @Test
  public void testUNHWithCollisionHashingActivationFrequencyWithRandom() {
    final ColisionDetection hashing = new ColisionDetection(new UNH(new Random(0), memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        AbstractPartitionFactory discretizerFactory = new WrappedPartitionFactory(buildRanges(nbInputs, 0, 1));
        discretizerFactory.setRandom(new Random(0), 0.1);
        TileCoders coders = new TileCodersHashing(hashing, discretizerFactory, nbInputs);
        return coders;
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }

  @Test
  public void testJavaHashingWithCollisionHashingActivationFrequency() {
    final ColisionDetection hashing = new ColisionDetection(new JavaHashing(memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        return new TileCodersHashing(hashing, new WrappedPartitionFactory(buildRanges(nbInputs, 0, 1)), nbInputs);
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }

  @Test
  public void testMurmurHashingWithCollisionHashingActivationFrequency() {
    final ColisionDetection hashing = new ColisionDetection(new MurmurHashing(new Random(0), memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        return new TileCodersHashing(hashing, new WrappedPartitionFactory(buildRanges(nbInputs, 0, 1)), nbInputs);
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }
}
