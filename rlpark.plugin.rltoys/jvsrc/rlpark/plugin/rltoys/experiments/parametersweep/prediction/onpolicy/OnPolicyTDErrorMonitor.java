package rlpark.plugin.rltoys.experiments.parametersweep.prediction.onpolicy;

import rlpark.plugin.rltoys.algorithms.predictions.td.TDErrorMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictorEvaluator;

public class OnPolicyTDErrorMonitor extends AbstractPerformanceMonitor implements PredictorEvaluator {
  public static double Precision = 1e-6;
  private final TDErrorMonitor monitor;
  private int timeOffset = -1;

  public OnPolicyTDErrorMonitor(double gamma, int nbBins, int nbEvaluationSteps) {
    super("", PredictionParameters.MSE, createStartingPoints(nbBins,
                                                             nbEvaluationSteps
                                                                 - TDErrorMonitor.computeBufferSize(gamma, Precision)));
    monitor = new TDErrorMonitor(gamma, Precision);
  }

  @Override
  public void registerPrediction(int time, double target, double prediction) {
    monitor.update(prediction, target, false);
    if (!monitor.errorComputed())
      return;
    if (timeOffset < 0)
      timeOffset = time;
    registerMeasurement(time - timeOffset, monitor.error() * monitor.error());
  }

  @Override
  protected double worstValue() {
    return Float.MAX_VALUE;
  }
}
