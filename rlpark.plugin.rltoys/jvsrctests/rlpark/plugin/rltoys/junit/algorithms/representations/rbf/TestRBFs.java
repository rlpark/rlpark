package rlpark.plugin.rltoys.junit.algorithms.representations.rbf;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.representations.rbf.RBFs;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

public class TestRBFs {
  private static final Range[] ranges = new Range[] { new Range(-1, 1), new Range(-1, 1) };
  private static final int Resolution = 10;
  private static final double StdDev = .0001;
  private static final double Precision = .1;

  @Test
  public void testNormAndSizeWhenIndependant() {
    RBFs rbfs = new RBFs(Precision);
    rbfs.addIndependentRBFs(ranges, Resolution, StdDev);
    Assert.assertEquals((double) Resolution * 2, rbfs.vectorNorm(), 0.0);
    Assert.assertEquals((double) Resolution * 2, rbfs.vectorSize(), 0.0);
    rbfs.includeActiveFeature();
    Assert.assertEquals((double) Resolution * 2 + 1, rbfs.vectorNorm(), 0.0);
    Assert.assertEquals((double) Resolution * 2 + 1, rbfs.vectorSize(), 0.0);
  }

  @Test
  public void testNormAndSizeWhenFull() {
    RBFs rbfs = new RBFs(Precision);
    rbfs.addFullRBFs(ranges, Resolution, StdDev);
    Assert.assertEquals((double) Resolution * Resolution, rbfs.vectorNorm(), 0.0);
    Assert.assertEquals((double) Resolution * Resolution, rbfs.vectorSize(), 0.0);
    rbfs.includeActiveFeature();
    Assert.assertEquals((double) Resolution * Resolution + 1, rbfs.vectorNorm(), 0.0);
    Assert.assertEquals((double) Resolution * Resolution + 1, rbfs.vectorSize(), 0.0);
  }

  @Test
  public void testValuesWhenIndependant() {
    RBFs rbfs = new RBFs(Precision);
    rbfs.addIndependentRBFs(ranges, Resolution, StdDev);
    SVector projected = rbfs.project(new double[] { 0.0, 0.0 });
    Assert.assertEquals(projected.nonZeroElements(), 0);
    projected = rbfs.project(new double[] { 0.5, 0.5 });
    Assert.assertEquals(projected.nonZeroElements(), 2);
  }

  @Test
  public void testValuesWhenFull() {
    RBFs rbfs = new RBFs(Precision);
    rbfs.addFullRBFs(ranges, Resolution, StdDev);
    SVector projected = rbfs.project(new double[] { 0.0, 0.0 });
    Assert.assertEquals(projected.nonZeroElements(), 0);
    projected = rbfs.project(new double[] { 0.5, 0.5 });
    Assert.assertEquals(projected.nonZeroElements(), 1);
  }
}
