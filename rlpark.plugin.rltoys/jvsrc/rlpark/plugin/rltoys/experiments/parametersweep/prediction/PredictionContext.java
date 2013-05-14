package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public interface PredictionContext extends Context {
  PredictionProblemFactory problemFactory();

  PredictionLearnerFactory learnerFactory();

  PredictorEvaluator createPredictorEvaluator(Parameters parameters);
}
