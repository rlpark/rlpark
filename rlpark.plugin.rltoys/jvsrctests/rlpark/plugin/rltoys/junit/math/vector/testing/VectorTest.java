package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;


public abstract class VectorTest {

  protected final RealVector a = newVector(0.0, 3.0, 2.0, 0.0, 1.0);
  protected final RealVector b = newVector(3.0, 4.0, 0.0, 0.0, 4.0);
  protected final BVector c = BVector.toBVector(2, new int[] { 1 });

  @After
  public void after() {
    VectorsTestsUtils.assertEquals(a, newVector(0.0, 3.0, 2.0, 0.0, 1.0));
    VectorsTestsUtils.assertEquals(b, newVector(3.0, 4.0, 0.0, 0.0, 4.0));
    VectorsTestsUtils.assertEquals(c, newVector(0.0, 1.0));
  }

  @Test
  public void testVectorVector() {
    RealVector c = newVector(a);
    VectorsTestsUtils.assertEquals(a, c);
    Assert.assertFalse(c.equals(b));
    Assert.assertFalse(a.equals(newVector(1.0)));
  }

  @Test
  public void testNewInstance() {
    RealVector v = a.newInstance(a.getDimension());
    VectorsTestsUtils.assertEquals(newPrototypeVector(a.getDimension()), v);
  }

  @Test
  public void testSetEntry() {
    MutableVector v = a.copyAsMutable();
    v.setEntry(1, 3);
    VectorsTestsUtils.assertEquals(newVector(0.0, 3.0, 2.0, 0.0, 1.0), v);
    v.setEntry(0, 0);
    v.setEntry(1, 0);
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 2.0, 0.0, 1.0), v);
  }

  @Test
  public void testSum() {
    Assert.assertEquals(6.0, a.sum(), 0.0);
    Assert.assertEquals(11.0, b.sum(), 0.0);
    Assert.assertEquals(1.0, c.sum(), 0.0);
  }

  @Test
  public void testDotProductPVector() {
    Assert.assertEquals(16.0, a.dotProduct(b), 0.0);
  }

  @Test
  public void testDotProductSVector() {
    Assert.assertEquals(16.0, a.dotProduct(newSVector(b)), 0.0);
    BVector cResized = new BVector(a.getDimension());
    cResized.set(c);
    Assert.assertEquals(3.0, a.dotProduct(cResized), 0.0);
  }

  @Test
  public void testClone() {
    RealVector ca = a.copy();
    Assert.assertNotSame(a, ca);
    VectorsTestsUtils.assertEquals(a, ca);
  }

  @Test
  public void testPlus() {
    VectorsTestsUtils.assertEquals(newVector(3.0, 7.0, 2.0, 0.0, 5.0), a.add(b));
  }

  @Test
  public void testMinus() {
    VectorsTestsUtils.assertEquals(newVector(-3.0, -1.0, 2.0, 0.0, -3.0), a.subtract(b));
    VectorsTestsUtils.assertEquals(newVector(-3.0, 2.0, 4.0, 0.0, -2.0), a.mapMultiply(2.0).subtract(b));
  }

  @Test
  public void testPlusSVector() {
    VectorsTestsUtils.assertEquals(newVector(3.0, 7.0, 2.0, 0.0, 5.0), a.add(newSVector(b)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 4.0, 2.0, 0.0, 1.0), a.add(c));
  }

  @Test
  public void testMinusSVector() {
    VectorsTestsUtils.assertEquals(newVector(-3.0, -1.0, 2.0, 0.0, -3.0), a.subtract(newSVector(b)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0), a.subtract(c));
  }

  @Test
  public void testMapTimes() {
    VectorsTestsUtils.assertEquals(newVector(0.0, 15.0, 10.0, 0.0, 5.0), a.mapMultiply(5.0));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.mapMultiply(0.0));
  }

  @Test
  public void testSubtractToSelf() {
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0),
                                   a.copyAsMutable().subtractToSelf(new PVector(0.0, 1.0, 0.0, 0.0, 0.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0),
                                   a.copyAsMutable().subtractToSelf(newSVector(0.0, 1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0),
                                   a.copyAsMutable().subtractToSelf(newSVector(0.0, 3.0, 2.0, 0.0, 1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.copyAsMutable().subtractToSelf(a));
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0), a.copyAsMutable().subtractToSelf(c));
  }

  @Test
  public void testAddToSelf() {
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0),
                                   a.copyAsMutable().addToSelf(new PVector(0.0, -1.0, 0.0, 0.0, 0.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 3.0, 2.0, 0.0, 0.0),
                                   a.copyAsMutable().addToSelf(new PVector(0.0, 0.0, 0.0, 0.0, -1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 2.0, 2.0, 0.0, 1.0),
                                   a.copyAsMutable().addToSelf(newSVector(0.0, -1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 0.0), a.copyAsMutable().addToSelf(a.mapMultiply(-1)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 4.0, 2.0, 0.0, 1.0), a.copyAsMutable().addToSelf(c));
  }

  @Test
  public void testEbeMultiply() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorsTestsUtils.assertEquals(new PVector(-3, 4, 10), a1.ebeMultiply(a2));
  }

  @Test
  public void testEbeMultiplySelf() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorsTestsUtils.assertEquals(new PVector(-3, 4, 10), a1.copyAsMutable().ebeMultiplyToSelf(a2));
    VectorsTestsUtils.assertEquals(new PVector(-1, 2, 6),
                                   a1.copyAsMutable().ebeMultiplyToSelf(new PVector(1.0, 2.0, 3.0)));
    RealVector a3 = newVector(0, 1, 0);
    RealVector a4 = newVector(-1, 0, 2);
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 0), a3.copyAsMutable().ebeMultiplyToSelf(a4));
    VectorsTestsUtils.assertEquals(new PVector(0, 0, 0), a4.copyAsMutable().ebeMultiplyToSelf(a3));

    PVector p1 = new PVector(2.0, 2.0, 2.0, 2.0, 2.0);
    VectorsTestsUtils.assertEquals(new PVector(0, 6.0, 4.0, 0.0, 2.0), p1.ebeMultiplyToSelf(a));
  }

  @Test
  public void testEbeMultiplySelf2() {
    MutableVector a = newVector(0, 1, 0, -2, 0, 3, 0).copyAsMutable();
    VectorsTestsUtils.assertEquals(new PVector(0, 1, 0, 4, 0, 9, 0), a.ebeMultiplyToSelf(a));
  }

  @Test
  public void testEbeDivideSelf() {
    MutableVector a2 = newSVector(3, 4, 0, -6);
    RealVector a1 = newVector(-1, -2, 2, 3);
    final PVector expected = new PVector(-3, -2, 0.0, -2.0);
    VectorsTestsUtils.assertEquals(expected, a2.ebeDivideToSelf(a1));
    a2 = new PVector(3, 4, 0, -6);
    VectorsTestsUtils.assertEquals(expected, a2.ebeDivideToSelf(a1));
  }

  @Test
  public void testMax() {
    RealVector a1 = newVector(2, -1, 1, 3, 1);
    MutableVector sa2 = newSVector(1, 0, -2, 0, -3);
    MutableVector pa2 = new PVector(sa2.accessData());
    final PVector expected = new PVector(2, 0, 1, 3, 1);
    VectorsTestsUtils.assertEquals(expected, Vectors.maxToSelf(sa2, a1));
    VectorsTestsUtils.assertEquals(expected, Vectors.maxToSelf(pa2, a1));
  }

  @Test
  public void testPositiveMax() {
    RealVector a1 = newVector(2, 1, 1, 3, 1);
    MutableVector sa2 = newSVector(1, 0, 2, 0, 3);
    MutableVector pa2 = new PVector(sa2.accessData());
    final PVector expected = new PVector(2, 1, 2, 3, 3);
    VectorsTestsUtils.assertEquals(expected, Vectors.positiveMaxToSelf(sa2, a1));
    VectorsTestsUtils.assertEquals(expected, Vectors.positiveMaxToSelf(pa2, a1));
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
    VectorsTestsUtils.assertEquals(newVector(1.0, 7.0, 5.0, 1.0, 3.0), v);
    v.addToSelf(-1.0, b);
    VectorsTestsUtils.assertEquals(newVector(-2.0, 3.0, 5.0, 1.0, -1.0), v);
  }

  @Test
  public void testMultiplySelfByExponential() {
    PVector v1 = new PVector(0.0, 1.0, 2.0, 3.0, 4.0);
    RealVector v2 = newVector(1.0, -2.0, -3.0, 0.0, 2.0);
    PVectors.multiplySelfByExponential(v1, 1.0, v2);
    VectorsTestsUtils.assertEquals(newVector(0.0, Math.exp(-2.0), 2.0 * Math.exp(-3.0), 3.0, 4 * Math.exp(2.0)), v1);
    PVectors.multiplySelfByExponential(v1, 1.0, newVector(-10000.0, -10000.0, -10000.0, -100000.0, -10000.0));
    Assert.assertTrue(Vectors.isNull(v1));
  }

  @Test
  public void testMultiplySelfByExponentialBounded() {
    PVector v1 = new PVector(0.0, 1.0, 2.0, 3.0, 4.0);
    RealVector v2 = newVector(1.0, -2.0, -3.0, 0.0, 2.0);
    PVectors.multiplySelfByExponential(v1, 1.0, v2);
    VectorsTestsUtils.assertEquals(newVector(0.0, Math.exp(-2.0), 2.0 * Math.exp(-3.0), 3.0, 4 * Math.exp(2.0)), v1);
    PVectors.multiplySelfByExponential(v1, 1.0, newVector(-10000.0, -10000.0, -10000.0, -100000.0, -10000.0), .1);
    Assert.assertFalse(Vectors.isNull(v1));
  }

  @Test
  public void testAbs() {
    MutableVector v = (MutableVector) newVector(1.0, -2.0, -3.0, 0.0, 2.0);
    VectorsTestsUtils.assertEquals(new PVector(1.0, 2.0, 3.0, 0.0, 2.0), Vectors.absToSelf(v));
  }

  @Test
  public void testSetPVector() {
    MutableVector v = (MutableVector) newVector(1.0, -2.0, -3.0, 0.0, 2.0);
    VectorsTestsUtils.assertEquals(v, new PVector(5).set(v));
  }

  @Test
  public void testToBinary() {
    MutableVector v = (MutableVector) newVector(1.0, 0.0, -3.0, 0.0, 2.0);
    BVector expected = BVector.toBVector(5, new int[] { 0, 2, 4 });
    VectorsTestsUtils.assertEquals(expected, Vectors.toBinary(newPrototypeVector(5), v));
  }

  @Test
  public void testL1Norm() {
    MutableVector v = (MutableVector) newVector(1.0, -2.0, -3.0, 0.0, 2.0);
    Assert.assertEquals(8.0, Vectors.l1Norm(v), 1e-8);
  }

  final protected RealVector newVector(double... values) {
    return newVector(newPrototypeVector(0), values);
  }

  final protected RealVector newVector(RealVector other) {
    return newVector(newPrototypeVector(0), other);
  }

  static protected SVector newSVector(double... values) {
    return (SVector) newVector(new SVector(0), values);
  }

  static protected SVector newSVector(RealVector other) {
    return (SVector) newVector(new SVector(0), other);
  }

  static protected RealVector newVector(MutableVector prototype, double... values) {
    MutableVector result = prototype.newInstance(values.length);
    for (int i = 0; i < values.length; i++)
      result.setEntry(i, values[i]);
    return result;
  }

  static protected RealVector newVector(MutableVector prototype, RealVector other) {
    MutableVector result = prototype.newInstance(other.getDimension());
    for (int i = 0; i < other.getDimension(); i++)
      result.setEntry(i, other.getEntry(i));
    return result;
  }

  protected abstract MutableVector newPrototypeVector(int size);
}