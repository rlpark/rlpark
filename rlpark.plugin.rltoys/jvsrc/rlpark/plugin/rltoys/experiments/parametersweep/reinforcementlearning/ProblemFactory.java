package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;
import java.util.Random;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.labels.Labeled;

public interface ProblemFactory extends Labeled, Serializable {
  RLProblem createEnvironment(Random random);

  void setExperimentParameters(Parameters parameters);
}
