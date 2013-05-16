package rlpark.plugin.rltoys.experiments.testing.predictions;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.experiments.testing.predictions.FiniteStateGraphOnPolicy.OnPolicyTDFactory;
import rlpark.plugin.rltoys.experiments.testing.predictions.RandomWalkOffPolicy.OffPolicyTDFactory;
import rlpark.plugin.rltoys.experiments.testing.results.TestingResult;

public abstract class OffPolicyTests extends OnPolicyTests {
  private static final double Gamma = 0.9;

  @Test
  public void testOffPolicy() {
    for (OffPolicyTDFactory factory : offPolicyTDFactory()) {
      testOffPolicy(0.0, 0.2, 0.5, factory);
      testOffPolicy(0.0, 0.5, 0.2, factory);
    }
  }

  @Test
  public void testOffPolicyWithLambda() {
    for (OffPolicyTDFactory factory : offPolicyTDFactory()) {
      for (double lambda : lambdaValues()) {
        testOffPolicy(lambda, 0.2, 0.5, factory);
        testOffPolicy(lambda, 0.5, 0.2, factory);
      }
    }
  }

  protected OffPolicyTDFactory[] offPolicyTDFactory() {
    OnPolicyTDFactory[] onPolicyTDFactories = onPolicyFactories();
    OffPolicyTDFactory[] offPolicyTDFactories = new OffPolicyTDFactory[onPolicyTDFactories.length];
    for (int i = 0; i < offPolicyTDFactories.length; i++) {
      final OnPolicyTDFactory onPolicyFactory = onPolicyTDFactories[i];
      offPolicyTDFactories[i] = new OffPolicyTDFactory() {
        @Override
        public OffPolicyTD newTD(double lambda, double gamma, double vectorNorm, int vectorSize) {
          return (OffPolicyTD) onPolicyFactory.create(lambda, gamma, vectorNorm, vectorSize);
        }
      };
    }
    return offPolicyTDFactories;
  }

  private void testOffPolicy(double lambda, double targetLeftProbability, double behaviourLeftProbability,
      OffPolicyTDFactory factory) {
    TestingResult<OffPolicyTD> result = RandomWalkOffPolicy.testOffPolicyGTD(nbEpisodeMax(), precision(), lambda,
                                                                             Gamma, targetLeftProbability,
                                                                             behaviourLeftProbability, factory);
    Assert.assertTrue(result.message, result.passed);
  }
}
