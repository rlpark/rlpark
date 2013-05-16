package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class K1 implements LearningAlgorithm {
  private static final long serialVersionUID = 2943574757813500087L;
  private final double theta;
  private final PVector weights;
  private final PVector alphas;
  private final PVector betas;
  private final PVector hs;
  private double delta;
  private double prediction;

  public K1(int size, double theta) {
    this.theta = theta;
    weights = new PVector(size);
    double initialAlpha = 0.1;
    betas = new PVector(size);
    betas.set(Math.log(initialAlpha));
    alphas = new PVector(size);
    hs = new PVector(size);
  }

  @Override
  public double learn(RealVector rx, double y_tp1) {
    PVector x = (PVector) rx;
    prediction = predict(x);
    delta = y_tp1 - prediction;
    double pnorm = 0.0;
    for (int i = 0; i < weights.size; i++) {
      betas.data[i] += theta * delta * x.data[i] * hs.data[i];
      alphas.data[i] = Math.exp(betas.data[i]);
      pnorm += alphas.data[i] * x.data[i] * x.data[i];
    }
    for (int i = 0; i < weights.size; i++) {
      double p_i = alphas.data[i] / (1 + pnorm);
      weights.data[i] += p_i * delta * x.data[i];
      hs.data[i] = (hs.data[i] + p_i * delta * x.data[i]) * Math.max(0, 1 - p_i * x.data[i] * x.data[i]);
    }
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  public RealVector alphas() {
    return alphas;
  }

  public RealVector h() {
    return hs;
  }
}
