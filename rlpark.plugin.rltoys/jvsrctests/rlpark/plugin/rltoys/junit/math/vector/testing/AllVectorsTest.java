package rlpark.plugin.rltoys.junit.math.vector.testing;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SSortedVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
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
        if (!VectorsTestsUtils.equals(mutables[0], mutables[1]))
          return false;
      return true;
    }

    public boolean allCopyEquals() {
      MutableVector first = mutables[0].copy();
      for (int i = 1; i < mutables.length; i++) {
        if (!VectorsTestsUtils.equals(first, mutables[1].copy()))
          return false;
      }
      return true;
    }

    public boolean sumEquals() {
      double first = mutables[0].sum();
      for (int i = 1; i < mutables.length; i++)
        if (Math.abs(first - mutables[i].sum()) > 1e-10)
          return false;
      return true;
    }

    public boolean allArrayDataEquals() {
      double[] first = mutables[0].accessData();
      for (int i = 1; i < mutables.length; i++)
        if (!arraysEquals(first, mutables[i].accessData()))
          return false;
      return true;
    }

    public boolean allEntryEquals() {
      for (int mutable = 1; mutable < mutables.length; mutable++)
        for (int i = 0; i < mutables[0].getDimension(); i++)
          if (mutables[mutable].getEntry(i) != mutables[mutable].getEntry(i))
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
      Assert.assertTrue(vectors.sumEquals());
    }
    Assert.assertTrue(vectors.allArrayDataEquals());
    Assert.assertTrue(vectors.allEntryEquals());
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
    final MutableVector[] mutables = new MutableVector[] { new PVector(vectorDimension), new SVector(vectorDimension),
        new SSortedVector(vectorDimension) };
    final RealVector[] args = new RealVector[] { new PVector(vectorDimension), new SVector(vectorDimension),
        new SSortedVector(vectorDimension), new BVector(vectorDimension) };
    for (int n = 0; n < 500; n++) {
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      for (MutableVector v : mutables)
        v.setEntry(position, value);
      new PVector(vectorDimension).setEntry(position, value);
      new SVector(vectorDimension).setEntry(position, value);
      new BVector(vectorDimension).setOn(position);
    }
    for (int n = 0; n < 500; n++) {
      int position = random.nextInt(vectorDimension);
      double value = random.nextDouble();
      for (MutableVector v : mutables)
        v.setEntry(position, value);
    }
    for (int n = 0; n < 500; n++) {
      for (RealVector arg : args) {
        if (arg instanceof BVector)
          ((BVector) arg).setOn(random.nextInt(vectorDimension));
        else
          ((MutableVector) arg).setEntry(random.nextInt(vectorDimension), random.nextDouble());
      }
    }
    return new VectorForTests(mutables, args);
  }
}
