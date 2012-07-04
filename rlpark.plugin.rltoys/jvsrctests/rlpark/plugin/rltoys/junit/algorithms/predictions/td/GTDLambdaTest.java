package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.experiments.testing.predictions.RandomWalkOffPolicy;
import rlpark.plugin.rltoys.experiments.testing.predictions.RandomWalkOffPolicy.OffPolicyTDFactory;
import rlpark.plugin.rltoys.experiments.testing.results.TestingResult;


public class GTDLambdaTest {
  @Test
  public void testOffPolicyGTD() {
    testOffPolicyGTD(0.0, 0.9, 0.01, 0.5, 0.2, 0.5);
    testOffPolicyGTD(0.0, 0.9, 0.01, 0.5, 0.5, 0.2);
  }

  @Test
  public void testOffPolicyGTDWithEligibility() {
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.0, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.5, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.5, 0.5, 0.2);
  }

  private void testOffPolicyGTD(double lambda, double gamma, final double alpha_v, final double alpha_w,
      double targetLeftProbability, double behaviourLeftProbability) {
    OffPolicyTDFactory tdFactory = new OffPolicyTDFactory() {
      @Override
      public OffPolicyTD newTD(double lambda, double gamma, int vectorSize) {
        return new GTDLambda(lambda, gamma, alpha_v, alpha_w, vectorSize, new AMaxTraces());
      }
    };
    TestingResult<OffPolicyTD> result = RandomWalkOffPolicy.testOffPolicyGTD(lambda, gamma, targetLeftProbability,
                                                                             behaviourLeftProbability, tdFactory);
    Assert.assertTrue(result.message, result.passed);
  }
}
