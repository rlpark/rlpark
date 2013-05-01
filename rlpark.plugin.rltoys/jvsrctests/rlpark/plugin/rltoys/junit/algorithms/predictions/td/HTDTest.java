package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.predictions.td.HTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.experiments.testing.predictions.OffPolicyTests;

public class HTDTest extends OffPolicyTests {

  @Override
  protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
    return new HTD(gamma, 0.1 / vectorNorm, .001 / vectorNorm, vectorSize);
  }

  @Override
  protected double[] lambdaValues() {
    return new double[] { 0 };
  }

  @Override
  protected double precision() {
    return 0.05;
  }
}
