package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.experiments.testing.predictions.OffPolicyTests;

public class GTDLambdaTest extends OffPolicyTests {

  @Override
  protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
    return new GTDLambda(lambda, gamma, 0.01 / vectorNorm, 0.5 / vectorNorm, vectorSize, new AMaxTraces());
  }

  @Override
  protected double[] lambdaValues() {
    return new double[] { .1, .2 };
  }

  @Override
  protected double precision() {
    return 0.05;
  }
}
