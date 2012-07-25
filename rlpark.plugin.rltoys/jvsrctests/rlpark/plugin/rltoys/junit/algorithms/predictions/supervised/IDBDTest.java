package rlpark.plugin.rltoys.junit.algorithms.predictions.supervised;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.Autostep;
import rlpark.plugin.rltoys.algorithms.predictions.supervised.IDBD;
import rlpark.plugin.rltoys.experiments.testing.predictions.NoisyInputSumEvaluation;

public class IDBDTest {
  @Test
  public void testIDBD() {
    double error = NoisyInputSumEvaluation.evaluateLearner(new IDBD(NoisyInputSumEvaluation.NbInputs, 0.001));
    Assert.assertEquals(2.0, error, 0.1);
    error = NoisyInputSumEvaluation.evaluateLearner(new IDBD(NoisyInputSumEvaluation.NbInputs, 0.01));
    Assert.assertEquals(1.5, error, 0.1);
  }

  @Test
  public void testAutostep() {
    double error = NoisyInputSumEvaluation.evaluateLearner(new Autostep(NoisyInputSumEvaluation.NbInputs));
    Assert.assertEquals(2.1, error, 0.1);
  }
}
