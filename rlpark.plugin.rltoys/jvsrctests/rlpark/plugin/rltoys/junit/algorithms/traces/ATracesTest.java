package rlpark.plugin.rltoys.junit.algorithms.traces;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.MaxLengthTraces;
import rlpark.plugin.rltoys.algorithms.traces.RTraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.junit.math.vector.testing.VectorsTestsUtils;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.SSortedVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

public class ATracesTest {
  private static final BVector s01 = BVector.toBVector(100, new int[] { 1, 2, 3 });
  private static final BVector s02 = BVector.toBVector(100, new int[] { 4, 5, 6 });

  @Test
  public void testATraces() {
    double lambda = .9;
    testTraces(lambda, new ATraces(), 1.0 / (1.0 - lambda));
    testTraces(lambda, new ATraces(new SVector(0)), 1.0 / (1.0 - lambda));
  }

  @Test
  public void testRTraces() {
    testTraces(.9, new RTraces(), 1.0);
    testTraces(.9, new RTraces(new SVector(0)), 1.0);
  }

  @Test
  public void testAMaxTraces() {
    testTraces(.9, new AMaxTraces(5.0), 5.0);
    testTraces(.9, new AMaxTraces(new SVector(0), 5.0), 5.0);
  }

  private void testTraces(double lambda, Traces prototype, double expected) {
    testTracesWithDiscounting(lambda, prototype, expected);
    if (prototype.newTraces(1).vect() instanceof SSortedVector) {
      testTracesWithDiscounting(lambda, new MaxLengthTraces(prototype, s01.size), expected);
      testTracesWithDiscounting(lambda, new MaxLengthTraces(prototype, s02.nonZeroElements()), expected);
    }
  }

  private Traces testTracesWithDiscounting(double lambda, Traces prototype, double expected) {
    Traces traces = prototype.newTraces(100);
    traces.update(lambda, s01);
    VectorsTestsUtils.assertEquals(traces.vect(), new SVector(s01, 1.0));
    for (int i = 0; i < 1000; i++) {
      traces.update(lambda, s02);
      Assert.assertTrue(VectorsTestsUtils.checkConsistency(traces.vect()));
    }
    Assert.assertEquals(s02.nonZeroElements(), ((SparseVector) traces.vect()).nonZeroElements());
    VectorsTestsUtils.assertEquals(traces.vect(), new SVector(s02, expected), 0.00001);
    return traces;
  }
}
