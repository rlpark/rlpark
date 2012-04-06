package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.junit.math.vector.testing.VectorTest;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;


public class TileCodersNoHashingTest {
  interface TileCodersFactory {
    TileCoders create(int nbInputs, double min, double max);
  }

  @Test
  public void testTileCodersIndependentDim2() {
    TileCoders coders = new TileCodersNoHashing(2, -1, 1);
    coders.addIndependentTilings(2, 1);
    Assert.assertEquals(4, coders.vectorSize());
    Assert.assertEquals(2.0, coders.vectorNorm(), 0.0);
    coders.project(new double[] { -0.5, 0.5 });
    VectorTest.assertEquals(new PVector(1, 0, 0, 1), coders.vector());
    coders.project(new double[] { 0.5, -0.5 });
    VectorTest.assertEquals(new PVector(0, 1, 1, 0), coders.vector());
  }

  @Test
  public void testTileCodersIndependentDim3() {
    TileCoders coders = new TileCodersNoHashing(3, -1, 1);
    coders.addIndependentTilings(2, 1);
    Assert.assertEquals(6, coders.vectorSize());
    Assert.assertEquals(3.0, coders.vectorNorm(), 0.0);
    coders.project(new double[] { -0.5, 0.5, 0.0 });
    VectorTest.assertEquals(new PVector(1, 0, 0, 1, 0, 1), coders.vector());
    coders.project(new double[] { 0.5, -0.5, 0.0 });
    VectorTest.assertEquals(new PVector(0, 1, 1, 0, 0, 1), coders.vector());
  }

  @Test
  public void testTileCodersFullDim2() {
    TileCoders coders = new TileCodersNoHashing(2, -1, 1);
    coders.addFullTilings(2, 3);
    Assert.assertEquals(2 * 2 * 3, coders.vectorSize());
    Assert.assertEquals(3.0, coders.vectorNorm(), 0.0);
    coders.project(new double[] { 0.0, 0.0 });
    VectorTest.assertEquals(new PVector(0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0), coders.vector());
    coders.project(new double[] { -0.9, -0.9 });
    VectorTest.assertEquals(new PVector(1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1), coders.vector());
    coders.project(new double[] { 0.9, 0.9 });
    VectorTest.assertEquals(new PVector(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1), coders.vector());
    coders.project(new double[] { -0.1, 0.5 });
    VectorTest.assertEquals(new PVector(0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0), coders.vector());
    coders.project(new double[] { 0.5, -0.5 });
    VectorTest.assertEquals(new PVector(0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0), coders.vector());
  }

  @Test
  public void testTileCodersFullDim2WithAlwaysActiveFeature() {
    TileCoders coders = new TileCodersNoHashing(2, -1, 1);
    coders.addFullTilings(2, 1);
    coders.includeActiveFeature();
    Assert.assertEquals(2 * 2 + 1, coders.vectorSize());
    Assert.assertEquals(1 + 1, coders.vectorNorm(), 0.0);
    coders.project(new double[] { 0.1, 0.1 });
    VectorTest.assertEquals(new PVector(0, 0, 0, 1, 1), coders.vector());
    coders.project(new double[] { -0.1, -0.1 });
    VectorTest.assertEquals(new PVector(1, 0, 0, 0, 1), coders.vector());
  }

  @Test
  public void testTileCodersActivationFrequency() {
    checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        TileCoders coders = new TileCodersNoHashing(nbInputs, 0, 1);
        return coders;
      }
    });
  }

  @Test
  public void testTileCodersActivationFrequencyWithRandom() {
    int missingTiles = checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        PartitionFactory discretizerFactory = new PartitionFactory(true, 0, 1, nbInputs);
        discretizerFactory.setRandom(new Random(0), 0.1);
        TileCoders coders = new TileCodersNoHashing(discretizerFactory, nbInputs);
        return coders;
      }
    });
    Assert.assertEquals(0, missingTiles);
  }

  static protected int checkFeatureActivationFrequency(TileCodersFactory tileCodersFactory) {
    double[] inputs = new double[2];
    TileCoders coders = tileCodersFactory.create(inputs.length, 0, 1);
    int gridResolution = 50;
    int nbTilings = 5;
    coders.addFullTilings(gridResolution, nbTilings);
    int[] frequencies = new int[coders.vectorSize()];
    int step = gridResolution * 2;
    for (int i = 0; i < step; i++)
      for (int j = 0; j < step; j++) {
        inputs[0] = (float) i / step;
        inputs[1] = (float) j / step;
        BinaryVector vector = coders.project(inputs);
        for (int activeIndex : vector.activeIndexes())
          frequencies[activeIndex]++;
      }
    int sum = 0;
    int nbActivated = 0;
    for (int f : frequencies) {
      if (f != 0)
        nbActivated++;
      sum += f;
    }
    int nbSamples = step * step;
    Assert.assertEquals(nbSamples * coders.vectorNorm(), sum, 0.0);
    int nbTiles = (int) (Math.pow(gridResolution, inputs.length) * nbTilings);
    assert nbTiles >= nbActivated;
    Assert.assertTrue(nbActivated > gridResolution * gridResolution);
    return nbTiles - nbActivated;
  }
}
