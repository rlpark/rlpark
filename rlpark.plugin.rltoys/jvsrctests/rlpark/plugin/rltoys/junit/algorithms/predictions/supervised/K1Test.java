package rlpark.plugin.rltoys.junit.algorithms.predictions.supervised;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.K1;
import rlpark.plugin.rltoys.experiments.testing.predictions.NoisyInputSumEvaluation;

public class K1Test {
  @Test
  public void testK1() {
    double error = NoisyInputSumEvaluation.evaluateLearner(new K1(NoisyInputSumEvaluation.NbInputs, 0.001));
    Assert.assertEquals(1.5, error, 0.1);
    error = NoisyInputSumEvaluation.evaluateLearner(new K1(NoisyInputSumEvaluation.NbInputs, 0.01));
    Assert.assertEquals(1.0, error, 0.1);
  }
}
