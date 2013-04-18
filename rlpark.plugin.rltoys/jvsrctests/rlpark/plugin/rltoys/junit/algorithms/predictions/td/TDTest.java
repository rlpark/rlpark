package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.experiments.testing.predictions.OnPolicyTests;


public class TDTest extends OnPolicyTests {

  @Override
  protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
    return new TD(gamma, .01 / vectorNorm, vectorSize);
  }

  @Override
  protected double[] lambdaValues() {
    return new double[] {};
  }


  @Override
  protected double precision() {
    return 0.01;
  }
}
