package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambdaAutostep;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.experiments.testing.predictions.OnPolicyTests;

public class TDLambdaAutostepTest extends OnPolicyTests {

  @Override
  protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
    return new TDLambdaAutostep(lambda, gamma, vectorSize, new ATraces());
  }

  @Override
  protected double[] lambdaValues() {
    return new double[] { .1 };
  }

  @Override
  protected double precision() {
    return 0.05;
  }
}
