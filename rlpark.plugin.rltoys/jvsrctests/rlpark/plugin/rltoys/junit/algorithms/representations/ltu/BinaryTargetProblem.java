package rlpark.plugin.rltoys.junit.algorithms.representations.ltu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.utils.Utils;

public class BinaryTargetProblem {
  final public int inputSize;
  private final double[] weights;
  private final Random random;

  public BinaryTargetProblem(Random random, int nbRelevantInput, int inputSize) {
    this.inputSize = inputSize;
    this.random = random;
    weights = chooseWeights(random, nbRelevantInput);
  }

  private double[] chooseWeights(Random random, int nbRelevantInput) {
    double[] weights = new double[inputSize];
    for (int i = 0; i < weights.length; i++)
      weights[i] = i < nbRelevantInput ? random.nextDouble() : 0.0;
    return weights;
  }

  public double evaluateLearning(int nbTimeStep, LearningAlgorithm learner) {
    double squaredErrorSum = 0.0;
    for (int t = 0; t < nbTimeStep; t++) {
      BVector input = createInput();
      double target = computeTarget(input);
      double error = target - learner.predict(input);
      squaredErrorSum += error * error;
      learner.learn(input, target);
    }
    return squaredErrorSum / nbTimeStep;
  }

  private double computeTarget(BinaryVector input) {
    double result = 0;
    for (int activeIndex : input.getActiveIndexes())
      result += weights[activeIndex];
    return result;
  }

  protected BVector createInput() {
    List<Integer> input = new ArrayList<Integer>();
    for (int i = 0; i < inputSize; i++)
      if (random.nextBoolean())
        input.add(i);
    return BVector.toBVector(inputSize, Utils.asIntArray(input));
  }
}
