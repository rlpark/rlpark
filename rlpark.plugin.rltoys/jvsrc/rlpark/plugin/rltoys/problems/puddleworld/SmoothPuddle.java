package rlpark.plugin.rltoys.problems.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import rlpark.plugin.rltoys.utils.Utils;


public class SmoothPuddle implements ContinuousFunction {
  private final int[] patternIndexes;
  private final double[] meanValues;
  private final double[] varianceValues;

  public SmoothPuddle(int[] patternIndexes, double[] meanValues, double[] stddevValues) {
    this.patternIndexes = patternIndexes;
    this.meanValues = meanValues;
    varianceValues = new double[patternIndexes.length];
    for (int i = 0; i < stddevValues.length; i++) {
      varianceValues[i] = stddevValues[i] * stddevValues[i];
      assert varianceValues[i] > 0;
    }
  }

  @Override
  public double value(double[] input) {
    double result = 1.0;
    for (int i = 0; i < patternIndexes.length; i++)
      result *= normalDistribution(input[patternIndexes[i]], meanValues[i], varianceValues[i]);
    assert Utils.checkValue(result);
    return result;
  }

  private double normalDistribution(double x, double mu, double var) {
    double diff = x - mu;
    return (1.0 / Math.sqrt(2 * Math.PI * var)) * Math.exp(-(diff * diff) / (2 * var));
  }
}
