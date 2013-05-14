package rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;

public class ErrorMonitor extends AbstractPerformanceMonitor {
  public static final String MSE = "MSE";

  public ErrorMonitor(int nbBins, int nbEvaluationSteps) {
    super("", MSE, createStartingPoints(nbBins, nbEvaluationSteps));
  }

  public void registerPrediction(int time, double target, double prediction) {
    double diff = target - prediction;
    registerMeasurement(time, diff * diff);
  }

  @Override
  protected double worstValue() {
    return Float.MAX_VALUE;
  }
}
