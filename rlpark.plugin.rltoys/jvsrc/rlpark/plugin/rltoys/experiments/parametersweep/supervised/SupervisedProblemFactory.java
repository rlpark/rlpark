package rlpark.plugin.rltoys.experiments.parametersweep.supervised;

import java.io.Serializable;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.SupervisedProblem;
import zephyr.plugin.core.api.labels.Labeled;

public interface SupervisedProblemFactory extends Labeled, Serializable {
  SupervisedProblem createProblem(int counter, Parameters parameters);
}
