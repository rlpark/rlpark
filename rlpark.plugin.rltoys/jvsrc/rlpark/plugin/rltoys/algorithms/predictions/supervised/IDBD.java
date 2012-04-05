package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class IDBD implements LearningAlgorithm {
  private static final long serialVersionUID = 6961877310325699208L;
  private final double theta;
  private final PVector weights;
  private final PVector betas;
  private final PVector hs;

  public IDBD(int size, double theta) {
    this.theta = theta;
    weights = new PVector(size);
    betas = new PVector(size);
    betas.set(Math.log(0.1 / size));
    hs = new PVector(size);
  }

  @Override
  public double learn(RealVector rx, double y) {
    PVector x = (PVector) rx;
    double delta = y - predict(x);
    for (int i = 0; i < weights.size; i++) {
      betas.data[i] += theta * delta * x.data[i] * hs.data[i];
      double alpha_i = Math.exp(betas.data[i]);
      weights.data[i] += alpha_i * delta * x.data[i];
      hs.data[i] = hs.data[i] * Math.max(0, 1 - alpha_i * x.data[i] * x.data[i]) + alpha_i * delta * x.data[i];
    }
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }
}
