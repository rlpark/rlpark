package rlpark.plugin.rltoys.experiments.parametersweep.supervised;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface SupervisedLearnerFactory extends Labeled, Serializable {
  LearningAlgorithm createLearner(int counter, Parameters parameters);
}
