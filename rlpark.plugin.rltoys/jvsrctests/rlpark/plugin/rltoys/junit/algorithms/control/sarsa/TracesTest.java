package rlpark.plugin.rltoys.junit.algorithms.control.sarsa;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.MaxLengthTraces;
import rlpark.plugin.rltoys.algorithms.traces.RTraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.junit.algorithms.control.sarsa.SarsaTest.SarsaControlFactory;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SSortedVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

public class TracesTest extends MountainCarOnPolicyTest {
  private void testTraces(final Traces traces) {
    runTestOnOnMountainCar(new SarsaControlFactory(traces));
  }

  private void testTraces(MutableVector prototype) {
    testTraces(new ATraces(prototype));
    testTraces(new AMaxTraces(prototype));
  }

  @Test
  public void testSarsaOnMountainCarSVectorTraces() {
    testTraces(new SVector(0));
    testTraces(new SSortedVector(0));
    testTraces(new RTraces(new SVector(0)));
  }

  @Test
  public void testSarsaOnMountainCarPVectorTraces() {
    testTraces(new PVector(0));
  }

  @Test
  public void testSarsaOnMountainCarMaxLengthTraces() {
    testTraces(new MaxLengthTraces(new ATraces(new SSortedVector(0)), 100));
    testTraces(new MaxLengthTraces(new AMaxTraces(new SSortedVector(0)), 100));
    testTraces(new MaxLengthTraces(new RTraces(new SSortedVector(0)), 100));
  }
}
