package rlpark.plugin.rltoys.junit.math.vector.testing;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;


public class SVectorTest extends VectorTest {
  private final Random random = new Random(0);

  @Test
  public void testActiveIndexes() {
    Assert.assertArrayEquals(new int[] { 1, 2, 4 }, ((SVector) a).getActiveIndexes());
    Assert.assertArrayEquals(new int[] { 0, 1, 4 }, ((SVector) b).getActiveIndexes());
  }

  @Test
  public void addBVector() {
    SVector v = newPrototypeVector(10);
    BVector b = BVector.toBVector(10, new int[] { 1, 2, 3 });
    v.addToSelf(b);
    Assert.assertEquals(b.nonZeroElements(), v.nonZeroElements());
    Assert.assertTrue(VectorsTestsUtils.equals(b, v));
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
    SVector result = newPrototypeVector(size);
    int nbActive = random.nextInt(maxActive);
    for (int i = 0; i < nbActive; i++)
      result.setEntry(random.nextInt(size), random.nextDouble() * 2 - 1);
    Assert.assertTrue(VectorsTestsUtils.checkConsistency(result));
    return result;
  }

  @Override
  protected SVector newPrototypeVector(int size) {
    return new SVector(size);
  }

  @Test
  public void testSVectorSet() {
    final double[] a = new double[] { 1.0, 2.0, 3.0, 4.0 };
    SVector s = newSVector(a);
    final double[] b = new double[] { 0.0, 1.0, 0.0, 0.0 };
    s.set(newSVector(b));
    Assert.assertTrue(VectorsTestsUtils.checkConsistency(s));
    VectorsTestsUtils.assertEquals(new PVector(b), s);
    s.set(new PVector(3, 4), 1);
    Assert.assertTrue(VectorsTestsUtils.checkConsistency(s));
    VectorsTestsUtils.assertEquals(new PVector(new double[] { 0.0, 3.0, 4.0, 0.0 }), s);
  }
}
