package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;


public abstract class VectorTest {

  protected final RealVector a = newVector(0.0, 1.0, 2.0, 0.0, 3.0);
  protected final RealVector b = newVector(3.0, 4.0, 0.0, 0.0, 4.0);
  protected final BVector c = BVector.toBVector(2, new int[] { 1 });

  @After
  public void after() {
    VectorTest.assertEquals(a, newVector(0.0, 1.0, 2.0, 0.0, 3.0));
    VectorTest.assertEquals(b, newVector(3.0, 4.0, 0.0, 0.0, 4.0));
    VectorTest.assertEquals(c, newVector(0.0, 1.0));
  }

  @Test
  public void testVectorVector() {
    RealVector c = newVector(a);
    VectorTest.assertEquals(a, c);
    Assert.assertFalse(c.equals(b));
    Assert.assertFalse(a.equals(newVector(1.0)));
  }

  @Test
  public void testNewInstance() {
    RealVector v = a.newInstance(a.getDimension());
    VectorTest.assertEquals(newVector(a.getDimension()), v);
  }

  @Test
  public void testSetEntry() {
    MutableVector v = a.copyAsMutable();
    v.setEntry(1, 3);
    VectorTest.assertEquals(newVector(0.0, 3.0, 2.0, 0.0, 3.0), v);
    v.setEntry(0, 0);
    v.setEntry(1, 0);
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0), v);
  }

  @Test
  public void testSum() {
    Assert.assertEquals(6.0, Vectors.sum(a), 0.0);
    Assert.assertEquals(11.0, Vectors.sum(b), 0.0);
    Assert.assertEquals(1.0, Vectors.sum(c), 0.0);
  }

  @Test
  public void testDotProductPVector() {
    Assert.assertEquals(16.0, a.dotProduct(b), 0.0);
  }

  @Test
  public void testDotProductSVector() {
    Assert.assertEquals(16.0, a.dotProduct(newSVector(b)), 0.0);
    BVector cResized = new BVector(a.getDimension());
    cResized.setOrderedIndexes(c.activeIndexes());
    Assert.assertEquals(1.0, a.dotProduct(cResized), 0.0);
  }

  @Test
  public void testClone() {
    RealVector ca = a.copy();
    Assert.assertNotSame(a, ca);
    VectorTest.assertEquals(a, ca);
  }

  @Test
  public void testPlus() {
    VectorTest.assertEquals(newVector(3.0, 5.0, 2.0, 0.0, 7.0), a.add(b));
  }

  @Test
  public void testMinus() {
    VectorTest.assertEquals(newVector(-3.0, -3.0, 2.0, 0.0, -1.0), a.subtract(b));
    VectorTest.assertEquals(newVector(-3.0, -2.0, 4.0, 0.0, 2.0), a.mapMultiply(2.0).subtract(b));
  }

  @Test
  public void testPlusSVector() {
    VectorTest.assertEquals(newVector(3.0, 5.0, 2.0, 0.0, 7.0), a.add(newSVector(b)));
    VectorTest.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 3.0), a.add(c));
  }

  @Test
  public void testMinusSVector() {
    VectorTest.assertEquals(newVector(-3.0, -3.0, 2.0, 0.0, -1.0), a.subtract(newSVector(b)));
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0), a.subtract(c));
  }

  @Test
  public void testMapTimes() {
    VectorTest.assertEquals(newVector(0.0, 5.0, 10.0, 0.0, 15.0), a.mapMultiply(5.0));
    VectorTest.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.mapMultiply(0.0));
  }

  @Test
  public void testSubtractToSelf() {
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0),
                            a.copyAsMutable().subtractToSelf(new PVector(0.0, 1.0, 0.0, 0.0, 0.0)));
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0), a.copyAsMutable().subtractToSelf(newSVector(0.0, 1.0)));
    VectorTest.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.copyAsMutable().subtractToSelf(a));
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0), a.copyAsMutable().subtractToSelf(c));
  }

  @Test
  public void testAddToSelf() {
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0),
                            a.copyAsMutable().addToSelf(new PVector(0.0, -1.0, 0.0, 0.0, 0.0)));
    VectorTest.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 3.0), a.copyAsMutable().addToSelf(newSVector(0.0, -1.0)));
    VectorTest.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.copyAsMutable().addToSelf(a.mapMultiply(-1)));
    VectorTest.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 3.0), a.copyAsMutable().addToSelf(c));
  }

  @Test
  public void testEbeMultiply() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorTest.assertEquals(new PVector(-3, 4, 10), a1.ebeMultiply(a2));
  }

  @Test
  public void testEbeMultiplySelf() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorTest.assertEquals(new PVector(-3, 4, 10), a1.copyAsMutable().ebeMultiplyToSelf(a2));
    VectorTest.assertEquals(new PVector(-1, 2, 6), a1.copyAsMutable().ebeMultiplyToSelf(new PVector(1.0, 2.0, 3.0)));
    RealVector a3 = newVector(0, 1, 0);
    RealVector a4 = newVector(-1, 0, 2);
    VectorTest.assertEquals(new PVector(0, 0, 0), a3.copyAsMutable().ebeMultiplyToSelf(a4));
    VectorTest.assertEquals(new PVector(0, 0, 0), a4.copyAsMutable().ebeMultiplyToSelf(a3));

    PVector p1 = new PVector(2.0, 2.0, 2.0, 2.0, 2.0);
    VectorTest.assertEquals(new PVector(0, 2.0, 4.0, 0.0, 6.0), p1.ebeMultiplyToSelf(a));
  }

  @Test
  public void testCheckValue() {
    Assert.assertTrue(Vectors.checkValues(newVector(1.0, 1.0)));
    Assert.assertFalse(Vectors.checkValues(newVector(1.0, Double.NaN)));
    Assert.assertFalse(Vectors.checkValues(newVector(1.0, Double.POSITIVE_INFINITY)));
  }

  @Test
  public void testToString() {
    newVector(4, 1.0).toString();
  }

  @Test
  public void testAddToSelfWithFactor() {
    PVector v = new PVector(1.0, 1.0, 1.0, 1.0, 1.0);
    v.addToSelf(2.0, a);
    VectorTest.assertEquals(newVector(1.0, 3.0, 5.0, 1.0, 7.0), v);
    v.addToSelf(-1.0, b);
    VectorTest.assertEquals(newVector(-2.0, -1.0, 5.0, 1.0, 3.0), v);
  }

  protected abstract RealVector newVector(RealVector v);

  protected abstract RealVector newVector(double... d);

  protected abstract RealVector newVector(int s);

  public static void assertEquals(RealVector a, RealVector b) {
    Assert.assertTrue(Vectors.equals(a, b));
    Assert.assertArrayEquals(a.accessData(), b.accessData(), Float.MIN_VALUE);
  }

  public static SVector newSVector(double... values) {
    SVector result = new SVector(values.length, values.length);
    for (int i = 0; i < values.length; i++)
      result.setEntry(i, values[i]);
    return result;
  }

  public static SVector newSVector(RealVector other) {
    SVector result = new SVector(other.getDimension());
    for (VectorEntry entry : other)
      result.setEntry(entry.index(), entry.value());
    return result;
  }
}