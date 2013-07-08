package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.PredictionProblem;
import zephyr.plugin.core.api.labels.Labeled;

public interface PredictionLearnerFactory extends Labeled, Serializable {
  Predictor createLearner(long seed, PredictionProblem problem, Parameters parameters);
}
