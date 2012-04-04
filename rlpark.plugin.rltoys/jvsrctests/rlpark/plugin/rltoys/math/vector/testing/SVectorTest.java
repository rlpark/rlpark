package rlpark.plugin.rltoys.math.vector.testing;

import java.util.Iterator;
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


public class SVectorTest extends VectorTest {
  private final Random random = new Random(0);

  @Test
  public void testIteratorRemove() {
    MutableVector v = a.copyAsMutable();
    Iterator<VectorEntry> iterator = v.iterator();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator = v.iterator();
    iterator.next();
    iterator.remove();
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 3.0), v);
  }

  @Test
  public void testActiveIndexes() {
    Assert.assertArrayEquals(new int[] { 1, 2, 4 }, ((SVector) a).activeIndexes());
    Assert.assertArrayEquals(new int[] { 0, 1, 4 }, ((SVector) b).activeIndexes());
  }

  @Test
  public void addBVector() {
    SVector v = new SVector(10);
    BVector b = BVector.toBVector(10, new int[] { 1, 2, 3 });
    v.addToSelf(b);
    Assert.assertEquals(b.nonZeroElements(), v.nonZeroElements());
    Assert.assertTrue(Vectors.equals(b, v));
  }

  @Override
  protected RealVector newVector(RealVector v) {
    return newSVector(v);
  }

  @Override
  protected RealVector newVector(double... d) {
    return newSVector(d);
  }

  @Override
  protected RealVector newVector(int s) {
    return new SVector(s);
  }

  @Test
  public void addRandomVectors() {
    int size = 10;
    int active = 4;
    for (int i = 0; i < 10000; i++) {
      SVector a = createRandomSVector(active, size);
      SVector b = createRandomSVector(active, size);
      testVectorOperation(a, b);
      testVectorOperation(a, new PVector(b.accessData()));
      testVectorOperation(a, createRandomBVector(active, size));
    }
  }

  private void testVectorOperation(SVector a, RealVector b) {
    PVector pa = new PVector(a.accessData());
    PVector pb = new PVector(b.accessData());
    VectorsTestsUtils.assertEquals(pa, a);
    VectorsTestsUtils.assertEquals(pb, b);
    VectorsTestsUtils.assertEquals(pa.add(pb), a.add(b));
    VectorsTestsUtils.assertEquals(pa.subtract(pb), a.subtract(b));
    VectorsTestsUtils.assertEquals(pa.ebeMultiply(pb), a.ebeMultiply(b));
    float factor = random.nextFloat();
    VectorsTestsUtils.assertEquals(pa.addToSelf(factor, pb), a.add(b.mapMultiply(factor)));
  }

  private BVector createRandomBVector(int maxActive, int size) {
    BVector result = new BVector(size);
    int nbActive = random.nextInt(maxActive);
    for (int i = 0; i < nbActive; i++)
      result.setOn(random.nextInt(size));
    return result;
  }

  private SVector createRandomSVector(int maxActive, int size) {
    SVector result = new SVector(size);
    int nbActive = random.nextInt(maxActive);
    for (int i = 0; i < nbActive; i++)
      result.setEntry(random.nextInt(size), random.nextDouble() * 2 - 1);
    return result;
  }
}
