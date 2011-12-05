package rltoys.environments.continuousgridworld;

import rltoys.algorithms.representations.LocalFeature;

public class LocalFeatureSumFunction implements ContinuousFunction {
  private final int nbDimension;
  private final LocalFeature[] features;
  private final double[] weights;

  public LocalFeatureSumFunction(int nbDimension, double[] weights, LocalFeature[] features) {
    this.nbDimension = nbDimension;
    this.weights = weights;
    this.features = features;
  }

  @Override
  public double fun(double[] input) {
    double sum = 0.0;
    for (int i = 0; i < features.length; i++)
      sum += weights[i] * features[i].value(input);
    return sum;
  }

  @Override
  public int nbDimension() {
    return nbDimension;
  }
}
