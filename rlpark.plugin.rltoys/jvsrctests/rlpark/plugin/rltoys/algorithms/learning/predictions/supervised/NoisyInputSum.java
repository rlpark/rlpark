package rlpark.plugin.rltoys.algorithms.learning.predictions.supervised;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.learning.predictions.LearningAlgorithm;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;

public class NoisyInputSum {
  public static final int NbInputs = 20;
  static protected final int NbNonZeroWeight = 5;

  private PVector createWeights(Random random) {
    PVector weights = new PVector(NbInputs);
    for (int i = 0; i < weights.size; i++)
      if (i < NbNonZeroWeight)
        weights.data[i] = random.nextBoolean() ? 1 : -1;
      else
        weights.data[i] = 0;
    return weights;
  }

  private double learningStep(Random random, LearningAlgorithm algorithm, PVector inputs, PVector weights) {
    for (int i = 0; i < inputs.size; i++)
      inputs.data[i] = random.nextGaussian();
    double target = weights.dotProduct(inputs);
    return algorithm.learn(inputs, target);
  }

  public double evaluateLearner(LearningAlgorithm algorithm) {
    final Random random = new Random(0);
    PVector inputs = new PVector(NbInputs);
    PVector weights = createWeights(random);
    for (int i = 0; i < 20000; i++) {
      changeWeight(random, weights, i);
      learningStep(random, algorithm, inputs, weights);
    }
    PVector errors = new PVector(10000);
    for (int i = 0; i < 10000; i++) {
      changeWeight(random, weights, i);
      double error = learningStep(random, algorithm, inputs, weights);
      errors.data[i] = error;
      assert Utils.checkValue(error);
    }
    double mse = errors.dotProduct(errors) / errors.size;
    assert Utils.checkValue(mse);
    return mse;
  }

  private void changeWeight(final Random random, PVector weights, int i) {
    if (i % 20 == 0) {
      int weightIndex = random.nextInt(NbNonZeroWeight);
      weights.data[weightIndex] = weights.data[weightIndex] * -1;
    }
  }
}
