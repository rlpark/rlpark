package rlpark.plugin.rltoys.experiments.parametersweep.prediction.supervised;

import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;

public class ErrorMonitor extends AbstractPerformanceMonitor {
  public ErrorMonitor(int nbBins, int nbEvaluationSteps) {
    super("", PredictionParameters.MSE, createStartingPoints(nbBins, nbEvaluationSteps));
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
