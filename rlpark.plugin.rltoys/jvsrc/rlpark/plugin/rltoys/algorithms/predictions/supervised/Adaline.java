package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
@SuppressWarnings("restriction")
public class Adaline implements LearningAlgorithm, LinearLearner {
  private static final long serialVersionUID = -1427180343679219960L;
  private final double alpha;
  @Monitor(level = 4)
  private final PVector weights;
  private double prediction;
  private double target;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double error;

  public Adaline(int size, double alpha) {
    weights = new PVector(size);
    this.alpha = alpha;
  }

  @Override
  public double learn(RealVector x_t, double y_tp1) {
    prediction = predict(x_t);
    target = y_tp1;
    error = target - prediction;
    weights.addToSelf(x_t.mapMultiply(alpha * error));
    return error;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  @Override
  public PVector weights() {
    return weights;
  }

  @Override
  public void resetWeight(int i) {
    weights.data[i] = 0;
  }

  @Override
  public double error() {
    return error;
  }
}
