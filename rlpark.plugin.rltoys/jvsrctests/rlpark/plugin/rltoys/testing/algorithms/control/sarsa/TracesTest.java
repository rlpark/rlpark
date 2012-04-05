package rlpark.plugin.rltoys.testing.algorithms.control.sarsa;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.MaxLengthTraces;
import rlpark.plugin.rltoys.algorithms.traces.RTraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.testing.algorithms.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.testing.algorithms.control.sarsa.SarsaTest.SarsaControlFactory;

public class TracesTest extends MountainCarOnPolicyTest {
  private void testTraces(final Traces traces) {
    runTestOnOnMountainCar(new SarsaControlFactory(traces));
  }

  private void testTraces(MutableVector prototype) {
    testTraces(new ATraces(prototype));
    testTraces(new AMaxTraces(1.0, prototype));
    if (prototype instanceof SparseVector) {
      testTraces(new ATraces(prototype, 100, 0.05));
      testTraces(new AMaxTraces(1.0, prototype, 100, 0.05));
    }
  }

  @Test
  public void testSarsaOnMountainCarSVectorTraces() {
    testTraces(new SVector(0));
    testTraces(new RTraces());
  }

  @Test
  public void testSarsaOnMountainCarPVectorTraces() {
    testTraces(new PVector(0));
  }

  @Test
  public void testSarsaOnMountainCarMaxLengthTraces() {
    testTraces(new MaxLengthTraces(new ATraces(new SVector(0)), 100));
    testTraces(new MaxLengthTraces(new AMaxTraces(1.0, new SVector(0)), 100));
    testTraces(new MaxLengthTraces(new RTraces(), 100));
  }
}
