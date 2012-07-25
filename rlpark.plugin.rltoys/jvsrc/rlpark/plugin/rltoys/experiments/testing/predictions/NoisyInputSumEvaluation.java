package rlpark.plugin.rltoys.experiments.testing.predictions;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.problems.noisyinputsum.NoisyInputSum;
import rlpark.plugin.rltoys.utils.Utils;

public class NoisyInputSumEvaluation {
  public static final int NbInputs = 20;
  public static final int NbNonZeroWeights = 5;

  static public double evaluateLearner(LearningAlgorithm algorithm, int learningEpisodes, int evaluationEpisodes) {
    NoisyInputSum noisyInputSum = new NoisyInputSum(new Random(0), NbNonZeroWeights, NbInputs);
    for (int i = 0; i < learningEpisodes; i++) {
      noisyInputSum.step();
      algorithm.learn(noisyInputSum.inputs(), noisyInputSum.target());
    }
    PVector errors = new PVector(evaluationEpisodes);
    for (int i = 0; i < evaluationEpisodes; i++) {
      noisyInputSum.step();
      errors.data[i] = algorithm.learn(noisyInputSum.inputs(), noisyInputSum.target());
      assert Utils.checkValue(errors.data[i]);
    }
    double mse = errors.dotProduct(errors) / errors.size;
    assert Utils.checkValue(mse);
    return mse;
  }

  static public double evaluateLearner(LearningAlgorithm algorithm) {
    return evaluateLearner(algorithm, 20000, 10000);
  }
}
