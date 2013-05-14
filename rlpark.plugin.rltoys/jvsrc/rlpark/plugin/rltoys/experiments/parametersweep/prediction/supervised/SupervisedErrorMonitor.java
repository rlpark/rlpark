package rlpark.plugin.rltoys.experiments.parametersweep.prediction.supervised;

import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictorEvaluator;

public class SupervisedErrorMonitor extends AbstractPerformanceMonitor implements PredictorEvaluator {
  public SupervisedErrorMonitor(int nbBins, int nbEvaluationSteps) {
    super("", PredictionParameters.MSE, createStartingPoints(nbBins, nbEvaluationSteps));
  }

  @Override
  public void registerPrediction(int time, double target, double prediction) {
    double diff = target - prediction;
    registerMeasurement(time, diff * diff);
  }

  @Override
  protected double worstValue() {
    return Float.MAX_VALUE;
  }
}
