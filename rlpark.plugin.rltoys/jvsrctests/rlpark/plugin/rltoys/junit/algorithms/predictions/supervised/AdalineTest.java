package rlpark.plugin.rltoys.junit.algorithms.predictions.supervised;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.Adaline;
import rlpark.plugin.rltoys.experiments.testing.predictions.NoisyInputSumEvaluation;
import rlpark.plugin.rltoys.math.History;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;


public class AdalineTest {
  @Test
  public void testAdaline() {
    Random random = new Random(0);
    PVector targetWeights = new PVector(1.0, 2.0);
    Adaline adaline = new Adaline(2, 0.05);
    learnTarget(random, targetWeights, adaline);
    Assert.assertEquals(1.0, adaline.weights().data[0], 1e-2);
    Assert.assertEquals(2.0, adaline.weights().data[1], 1e-2);
  }

  @Test
  public void testAdalineOnTracking() {
    double error = NoisyInputSumEvaluation.evaluateLearner(new Adaline(NoisyInputSumEvaluation.NbInputs, 0));
    Assert.assertEquals(NoisyInputSumEvaluation.NbNonZeroWeights, error, 0.2);
    error = NoisyInputSumEvaluation.evaluateLearner(new Adaline(NoisyInputSumEvaluation.NbInputs, 0.03));
    Assert.assertEquals(3.2, error, 0.2);
  }

  public static void learnTarget(Random random, PVector targetWeights, Adaline learner) {
    int nbUpdate = 0;
    double threshold = 1e-3;
    History history = new History(5);
    history.fill(threshold);
    PVector features = new PVector(targetWeights.size);
    double target = 0.0;
    while (history.sum() > threshold) {
      updateFeatures(random, features);
      target = targetWeights.dotProduct(features);
      double error = learner.predict(features) - target;
      Assert.assertTrue(Utils.checkValue(error));
      history.append(Math.abs(error));
      learner.learn(features, target);
      nbUpdate++;
      Assert.assertTrue(nbUpdate < 100000);
    }
    Assert.assertTrue(nbUpdate > 30);
    Assert.assertEquals(target, learner.predict(features), threshold * 10);
  }

  private static void updateFeatures(Random random, PVector features) {
    for (int i = 0; i < features.data.length; i++)
      features.data[i] = random.nextDouble();
  }
}
