package rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding;

import static rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders.buildRanges;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedBigPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedSmallPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.junit.math.vector.testing.VectorsTestsUtils;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;


public class TileCodersNoHashingTest {
  interface TileCodersFactory {
    TileCoders create(int nbInputs, double min, double max);
  }

  static private BVector vect(int size, int... orderedIndexes) {
    return BVector.toBVector(size, orderedIndexes);
  }

  @Test
  public void testTileCodersDim1() {
    TileCoders coders = new TileCodersNoHashing(new WrappedPartitionFactory(buildRanges(1, -1, 1)), 1);
    coders.addIndependentTilings(2, 3);
    Assert.assertEquals(6, coders.vectorSize());
    Assert.assertEquals(3.0, coders.vectorNorm(), 0.0);
    VectorsTestsUtils.assertEquals(vect(6, 1, 2 + 0, 4 + 0), coders.project(new double[] { 0.0 }));
    VectorsTestsUtils.assertEquals(vect(6, 0, 2 + 1, 4 + 1), coders.project(new double[] { -0.9 }));
    VectorsTestsUtils.assertEquals(vect(6, 1, 2 + 1, 4 + 1), coders.project(new double[] { 0.9 }));
    VectorsTestsUtils.assertEquals(vect(6, 0, 2 + 0, 4 + 0), coders.project(new double[] { -0.1 }));
    VectorsTestsUtils.assertEquals(vect(6, 0, 2 + 0, 4 + 1), coders.project(new double[] { -0.5 }));
    VectorsTestsUtils.assertEquals(vect(6, 1, 2 + 1, 4 + 0), coders.project(new double[] { 0.5 }));
  }

  @Test
  public void testTileCodersDim1Res2Tiling4() {
    TileCoders coders = new TileCodersNoHashing(new BoundedSmallPartitionFactory(buildRanges(1, -1, 1)), 1);
    coders.addIndependentTilings(2, 4);
    Assert.assertEquals(4 * 2, coders.vectorSize());
    Assert.assertEquals(4, coders.vectorNorm(), 0.0);
    VectorsTestsUtils.assertEquals(vect(8, 1, 2 + 1, 4 + 0, 6 + 0), coders.project(new double[] { 0.0 }));
    VectorsTestsUtils.assertEquals(vect(8, 0, 2 + 0, 4 + 0, 6 + 0), coders.project(new double[] { -0.9 }));
    VectorsTestsUtils.assertEquals(vect(8, 1, 2 + 1, 4 + 1, 6 + 1), coders.project(new double[] { 0.9 }));
    VectorsTestsUtils.assertEquals(vect(8, 1, 2 + 0, 4 + 0, 6 + 0), coders.project(new double[] { -0.1 }));
    VectorsTestsUtils.assertEquals(vect(8, 0, 2 + 0, 4 + 0, 6 + 0), coders.project(new double[] { -0.5 }));
    VectorsTestsUtils.assertEquals(vect(8, 1, 2 + 1, 4 + 1, 6 + 1), coders.project(new double[] { 0.5 }));
  }

  @Test
  public void testTileCodersDim1Res3Tiling4BigPartition() {
    TileCoders coders = new TileCodersNoHashing(new BoundedBigPartitionFactory(buildRanges(1, 0, 1)), 1);
    coders.addIndependentTilings(3, 4);
    Assert.assertEquals(4 * 3, coders.vectorSize());
    Assert.assertEquals(4, coders.vectorNorm(), 0.0);
    VectorsTestsUtils.assertEquals(vect(12, 0, 3 + 0, 6 + 0, 9 + 0), coders.project(new double[] { 0.0 }));
    VectorsTestsUtils.assertEquals(vect(12, 0, 3 + 0, 6 + 0, 9 + 0), coders.project(new double[] { 0.1 }));
    VectorsTestsUtils.assertEquals(vect(12, 2, 3 + 2, 6 + 2, 9 + 2), coders.project(new double[] { 0.9 }));
    VectorsTestsUtils.assertEquals(vect(12, 0, 3 + 0, 6 + 0, 9 + 1), coders.project(new double[] { 0.2 }));
    VectorsTestsUtils.assertEquals(vect(12, 1, 3 + 2, 6 + 2, 9 + 2), coders.project(new double[] { 0.8 }));
    VectorsTestsUtils.assertEquals(vect(12, 1, 3 + 1, 6 + 1, 9 + 1), coders.project(new double[] { 0.5 }));
  }

  @Test
  public void testTileCodersIndependentDim2() {
    TileCoders coders = new TileCodersNoHashing(2, -1, 1);
    coders.addIndependentTilings(2, 1);
    Assert.assertEquals(4, coders.vectorSize());
    Assert.assertEquals(2.0, coders.vectorNorm(), 0.0);
    VectorsTestsUtils.assertEquals(vect(4, 0, 2 + 1), coders.project(new double[] { -0.5, 0.5 }));
    VectorsTestsUtils.assertEquals(vect(4, 1, 2 + 0), coders.project(new double[] { 0.5, -0.5 }));
  }

  @Test
  public void testTileCodersIndependentDim3() {
    TileCoders coders = new TileCodersNoHashing(new WrappedPartitionFactory(buildRanges(3, -1, 1)), 3);
    coders.addIndependentTilings(2, 1);
    Assert.assertEquals(6, coders.vectorSize());
    Assert.assertEquals(3.0, coders.vectorNorm(), 0.0);
    coders.project(new double[] { -0.5, 0.5, 0.0 });
    VectorsTestsUtils.assertEquals(new PVector(1, 0, 0, 1, 0, 1), coders.vector());
    coders.project(new double[] { 0.5, -0.5, 0.0 });
    VectorsTestsUtils.assertEquals(new PVector(0, 1, 1, 0, 0, 1), coders.vector());
  }

  @Test
  public void testTileCodersFullDim2() {
    TileCoders coders = new TileCodersNoHashing(new WrappedPartitionFactory(buildRanges(3, -1, 1)), 2);
    coders.addFullTilings(2, 3);
    Assert.assertEquals(2 * 2 * 3, coders.vectorSize());
    Assert.assertEquals(3.0, coders.vectorNorm(), 0.0);
    coders.project(new double[] { 0.0, 0.0 });
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0), coders.vector());
    coders.project(new double[] { -0.9, -0.9 });
    VectorsTestsUtils.assertEquals(new PVector(1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1), coders.vector());
    coders.project(new double[] { 0.9, 0.9 });
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1), coders.vector());
    coders.project(new double[] { -0.1, 0.5 });
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0), coders.vector());
    coders.project(new double[] { 0.5, -0.5 });
    VectorsTestsUtils.assertEquals(new PVector(0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0), coders.vector());
  }

  @Test
  public void testTileCodersFullDim2WithAlwaysActiveFeature() {
    TileCoders coders = new TileCodersNoHashing(new WrappedPartitionFactory(buildRanges(2, -1, 1)), 2);
    coders.addFullTilings(2, 1);
    coders.includeActiveFeature();
    Assert.assertEquals(2 * 2 + 1, coders.vectorSize());
    Assert.assertEquals(1 + 1, coders.vectorNorm(), 0.0);
    coders.project(new double[] { 0.1, 0.1 });
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 0, 1, 1), coders.vector());
    coders.project(new double[] { -0.1, -0.1 });
    VectorsTestsUtils.assertEquals(new PVector(1, 0, 0, 0, 1), coders.vector());
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
        AbstractPartitionFactory discretizerFactory = new WrappedPartitionFactory(buildRanges(nbInputs, min, max));
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
        for (int activeIndex : vector.getActiveIndexes())
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

  public static void main(String[] args) {
    TileCoders coders = new TileCodersNoHashing(1, 0, 1);
    coders.addFullTilings(2, 4);
    System.out.println(coders.toString());
  }
}
