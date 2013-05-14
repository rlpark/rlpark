package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;

public interface PredictionContext extends Context {
  PredictionProblemFactory problemFactory();

  PredictionLearnerFactory learnerFactory();
}
