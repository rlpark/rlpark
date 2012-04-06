package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.RealVector;
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
    PVector v = newVector(2);
    v.set(84.0);
    VectorTest.assertEquals(v, newVector(84.0, 84.0));
    v.set(0.0);
    VectorTest.assertEquals(v, newVector(0.0, 0.0));
  }

  @Test
  public void testAddDataToSelf() {
    PVector v = new PVector(1.0, 1.0, 1.0, 1.0, 1.0);
    v.addToSelf(new PVector(a).data);
    VectorTest.assertEquals(new PVector(1.0, 2.0, 3.0, 1.0, 4.0), v);
  }

  @Test
  public void testSetPVector() {
    PVector v = newVector(b.getDimension());
    v.set(b);
    VectorTest.assertEquals(v, b);
  }

  @Test
  public void testSetDoubleArray() {
    PVector v = newVector(a.getDimension());
    v.set(new double[] { 0.0, 1.0, 2.0, 0.0, 3.0 });
    VectorTest.assertEquals(v, a);
  }

  @Override
  protected PVector newVector(int s) {
    return new PVector(s);
  }

  @Override
  protected PVector newVector(double... d) {
    return new PVector(d);
  }

  @Override
  protected PVector newVector(RealVector v) {
    return new PVector(v);
  }
}
