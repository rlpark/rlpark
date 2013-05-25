package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;


public class PVectorTest extends VectorTest {
  @Test
  public void testMean() {
    Assert.assertEquals(6.0 / 5.0, PVectors.mean((PVector) a), 0.0);
    Assert.assertEquals(11.0 / 5.0, PVectors.mean((PVector) b), 0.0);
  }

  @Test
  public void testSetDouble() {
    PVector v = newPrototypeVector(2);
    v.set(84.0);
    VectorsTestsUtils.assertEquals(v, newVector(84.0, 84.0));
    v.set(0.0);
    VectorsTestsUtils.assertEquals(v, newVector(0.0, 0.0));
  }

  @Test
  public void testAddDataToSelf() {
    PVector v = new PVector(1.0, 1.0, 1.0, 1.0, 1.0);
    v.addToSelf(new PVector(a).data);
    VectorsTestsUtils.assertEquals(new PVector(1.0, 4.0, 3.0, 1.0, 2.0), v);
  }

  @Test
  public void testSetDoubleArray() {
    PVector v = newPrototypeVector(a.getDimension());
    v.set(new double[] { 0.0, 3.0, 2.0, 0.0, 1.0 }, 0);
    VectorsTestsUtils.assertEquals(a, v);
    v.set(new double[] { -1.0, -1.0 }, 2);
    VectorsTestsUtils.assertEquals(new PVector(0.0, 3.0, -1.0, -1.0, 1.0), v);
  }

  @Test
  public void testMin() {
    Assert.assertEquals(0.0, new PVector(4, 3, 2, 1, 0).min(), 0.0);
    Assert.assertEquals(-100.0, new PVector(4, -100, 2, 1, 0).min(), 0.0);
  }

  @Override
  protected PVector newPrototypeVector(int size) {
    return new PVector(size);
  }
}
