package rlpark.plugin.rltoys.junit.math.vector.testing;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.utils.Utils;

public class AllVectorsTest {
  enum Operations {
    addToSelf, subtractToSelf, mapMultiplyToSelf, setEntry, ebeMultiplyToSelf
  };

  class VectorForTests {
    final MutableVector[] mutables;
    final RealVector[] args;

    VectorForTests(MutableVector[] mutables, RealVector[] args) {
      this.mutables = mutables;
      this.args = args;
    }

    public boolean allEquals() {
      for (int i = 1; i < mutables.length; i++)
        if (!Vectors.equals(mutables[0], mutables[1]))
          return false;
      return true;
    }

    public boolean allCopyEquals() {
      MutableVector first = mutables[0].copy();
      for (int i = 1; i < mutables.length; i++) {
        if (!Vectors.equals(first, mutables[1].copy()))
          return false;
      }
      return true;
    }

    public boolean allArrayDataEquals() {
      double[] first = mutables[0].accessData();
      for (int i = 1; i < mutables.length; i++)
        if (!arraysEquals(first, mutables[i].accessData()))
          return false;
      return true;
    }

    public boolean allIteratorEquals() {
      double[] first = toArrayWithIterator(mutables[0]);
      for (int i = 1; i < mutables.length; i++)
        if (!arraysEquals(first, toArrayWithIterator(mutables[i])))
          return false;
      return true;
    }

    private boolean arraysEquals(double[] a, double[] b) {
      if (a.length != b.length)
        return false;
      for (int i = 0; i < b.length; i++)
        if (a[i] != b[i])
          return false;
      return true;
    }

    private double[] toArrayWithIterator(MutableVector mutableVector) {
      double[] data = new double[mutableVector.getDimension()];
      for (VectorEntry entry : mutableVector)
        data[entry.index()] = entry.value();
      return data;
    }
  }

  @Test
  public void testRandomOperations() {
    final Random random = new Random(0);
    final int vectorDimension = 10000;
    VectorForTests vectors = createVectors(random, vectorDimension);
    Assert.assertTrue(vectors.allEquals());
    for (int i = 0; i < 1000; i++) {
      performOperation(random, vectorDimension, vectors);
      Assert.assertTrue(vectors.allEquals());
      Assert.assertTrue(vectors.allCopyEquals());
    }
    Assert.assertTrue(vectors.allArrayDataEquals());
    Assert.assertTrue(vectors.allIteratorEquals());
  }

  private void performOperation(final Random random, final int vectorDimension, VectorForTests vectors) {
    Operations operation = Utils.choose(random, Operations.values());
    RealVector arg = Utils.choose(random, vectors.args);
    switch (operation) {
    case addToSelf:
      for (MutableVector v : vectors.mutables)
        v.addToSelf(arg);
      break;
    case ebeMultiplyToSelf:
      for (MutableVector v : vectors.mutables)
        v.ebeMultiplyToSelf(arg);
      break;
    case mapMultiplyToSelf:
      double nextDouble = random.nextDouble();
      for (MutableVector v : vectors.mutables)
        v.mapMultiplyToSelf(nextDouble);
      break;
    case setEntry:
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      for (MutableVector v : vectors.mutables)
        v.setEntry(position, value);
      break;
    case subtractToSelf:
      for (MutableVector v : vectors.mutables)
        v.subtractToSelf(arg);
      break;
    }
  }

  private VectorForTests createVectors(Random random, int vectorDimension) {
    PVector v01 = new PVector(vectorDimension);
    SVector v02 = new SVector(vectorDimension);
    PVector arg01 = new PVector(vectorDimension);
    SVector arg02 = new SVector(vectorDimension);
    BVector arg03 = new BVector(vectorDimension);
    for (int n = 0; n < 500; n++) {
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      v01.setEntry(position, value);
      v02.setEntry(position, value);
      arg01.setEntry(position, value);
      arg02.setEntry(position, value);
      arg03.setOn(position);
    }
    for (int n = 0; n < 500; n++) {
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      v01.setEntry(position, value);
      v02.setEntry(position, value);
    }
    for (int n = 0; n < 500; n++) {
      arg01.setEntry(random.nextInt(vectorDimension), random.nextDouble());
      arg02.setEntry(random.nextInt(vectorDimension), random.nextDouble());
      arg03.setOn(random.nextInt(vectorDimension));
    }
    return new VectorForTests(new MutableVector[] { v01, v02 }, new RealVector[] { arg01, arg02, arg03 });
  }
}
