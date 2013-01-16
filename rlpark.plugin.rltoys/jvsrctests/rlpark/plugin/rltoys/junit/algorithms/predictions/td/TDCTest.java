package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDC;
import rlpark.plugin.rltoys.experiments.testing.predictions.OnPolicyTests;

public class TDCTest extends OnPolicyTests {
  @Override
  protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
    return new TDC(gamma, 0.01 / vectorNorm, 0.5 / vectorNorm, vectorSize);
  }

  @Override
  protected double[] lambdaValues() {
    return new double[] {};
  }

  @Override
  protected double precision() {
    return 0.05;
  }
}
