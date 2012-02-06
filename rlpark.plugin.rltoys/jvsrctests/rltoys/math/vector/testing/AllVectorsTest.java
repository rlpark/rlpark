package rltoys.math.vector.testing;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;
import rltoys.math.vector.implementations.Vectors;
import rltoys.utils.Utils;

public class AllVectorsTest {
  enum Operations {
    addToSelf, subtractToSelf, mapMultiplyToSelf, setEntry, ebeMultiplyToSelf
  };

  @Test
  public void testRandomOperations() {
    final Random random = new Random(0);
    final int vectorDimension = 10000;
    MutableVector[] vectors = createVectors(random, vectorDimension);
    Assert.assertTrue(Vectors.equals(vectors[0], vectors[1], 0.0));
    for (int i = 0; i < 1000; i++) {
      performOperation(random, vectorDimension, vectors);
      Assert.assertTrue(Vectors.equals(vectors[0], vectors[1], 0.0));
      Assert.assertTrue(Vectors.equals(vectors[0].copy(), vectors[1].copy(), 0.0));
    }
  }

  private void performOperation(final Random random, final int vectorDimension, MutableVector[] vectors) {
    Operations operation = Utils.choose(random, Operations.values());
    RealVector arg = random.nextBoolean() ? vectors[2] : vectors[3];
    switch (operation) {
    case addToSelf:
      vectors[0].addToSelf(arg);
      vectors[1].addToSelf(arg);
      break;
    case ebeMultiplyToSelf:
      vectors[0].ebeMultiplyToSelf(arg);
      vectors[1].ebeMultiplyToSelf(arg);
      break;
    case mapMultiplyToSelf:
      double nextDouble = random.nextDouble();
      vectors[0].mapMultiplyToSelf(nextDouble);
      vectors[1].mapMultiplyToSelf(nextDouble);
      break;
    case setEntry:
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      vectors[0].setEntry(position, value);
      vectors[1].setEntry(position, value);
      break;
    case subtractToSelf:
      vectors[0].subtractToSelf(arg);
      vectors[1].subtractToSelf(arg);
      break;
    }
  }

  private MutableVector[] createVectors(Random random, int vectorDimension) {
    MutableVector[] vectors = new MutableVector[] { new PVector(vectorDimension), new SVector(vectorDimension),
        new PVector(vectorDimension), new SVector(vectorDimension) };
    for (int n = 0; n < 500; n++) {
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      for (MutableVector v : vectors)
        v.setEntry(position, value);
    }
    for (int n = 0; n < 500; n++) {
      for (int i = 0; i < vectors.length; i++) {
        int position = random.nextInt(vectorDimension);
        double value = random.nextDouble();
        vectors[0].setEntry(position, value);
        vectors[1].setEntry(position, value);
      }
      for (int i = 2; i < vectors.length; i++)
        vectors[i].setEntry(random.nextInt(vectorDimension), random.nextDouble());
    }
    return vectors;
  }
}
