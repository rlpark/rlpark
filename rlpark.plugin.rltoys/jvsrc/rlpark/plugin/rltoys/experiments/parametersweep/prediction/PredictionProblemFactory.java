package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import java.io.Serializable;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.PredictionProblem;
import zephyr.plugin.core.api.labels.Labeled;

public interface PredictionProblemFactory extends Labeled, Serializable {
  PredictionProblem createProblem(int counter, Parameters parameters);
}
