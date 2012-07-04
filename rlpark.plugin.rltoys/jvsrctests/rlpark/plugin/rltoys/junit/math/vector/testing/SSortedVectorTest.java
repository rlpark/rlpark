package rlpark.plugin.rltoys.junit.math.vector.testing;

import rlpark.plugin.rltoys.math.vector.implementations.SSortedVector;


public class SSortedVectorTest extends SVectorTest {
  @Override
  protected SSortedVector newPrototypeVector(int size) {
    return new SSortedVector(size);
  }
}
