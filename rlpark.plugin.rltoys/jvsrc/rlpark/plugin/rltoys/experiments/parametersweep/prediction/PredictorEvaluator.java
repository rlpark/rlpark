package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;

public interface PredictorEvaluator extends PerformanceEvaluator {
  void registerPrediction(int time, double target, double prediction);
}
