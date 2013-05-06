package rlpark.plugin.rltoys.experiments.testing.predictions;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.noisyinputsum.NoisyInputSum;
import rlpark.plugin.rltoys.utils.Utils;

public class NoisyInputSumEvaluation {
  public static final int NbInputs = 20;
  public static final int NbNonZeroWeights = 5;

  static public double evaluateLearner(LearningAlgorithm algorithm, int learningEpisodes, int evaluationEpisodes) {
    NoisyInputSum noisyInputSum = new NoisyInputSum(new Random(0), NbNonZeroWeights, NbInputs);
    for (int i = 0; i < learningEpisodes; i++) {
      noisyInputSum.update();
      algorithm.learn(noisyInputSum.input(), noisyInputSum.target());
    }
    PVector errors = new PVector(evaluationEpisodes);
    for (int i = 0; i < evaluationEpisodes; i++) {
      noisyInputSum.update();
      errors.data[i] = algorithm.learn(noisyInputSum.input(), noisyInputSum.target());
      assert Utils.checkValue(errors.data[i]);
    }
    double mse = errors.dotProduct(errors) / errors.size;
    assert Utils.checkValue(mse);
    return mse;
  }

  static public double evaluateLearner(LearningAlgorithm algorithm) {
    return evaluateLearner(algorithm, 20000, 10000);
  }

  public static String infoString(RealVector v) {
    StringBuilder result = new StringBuilder();
    result.append("L1Norm: ");
    result.append(Vectors.l1Norm(v));
    result.append(" Ave Non Zero: ");
    double averageNonZero = average(v, 0, NbNonZeroWeights);
    result.append(averageNonZero);
    result.append(" Ave Zero: ");
    double averageZero = average(v, NbNonZeroWeights, NbInputs);
    result.append(averageZero);
    result.append(" Ratio=" + averageNonZero / averageZero);
    return result.toString();
  }

  private static double average(RealVector v, int start, int end) {
    return Vectors.l1Norm(v.ebeMultiply(mask(start, end))) / (end - start);
  }

  private static RealVector mask(int start, int end) {
    BVector mask = new BVector(NbInputs);
    for (int i = start; i < end; i++)
      mask.setOn(i);
    return mask;
  }
}
