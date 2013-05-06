package rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.SupervisedProblem;

public interface SupervisedContext extends Context {
  SupervisedProblem createProblem(int counter, Parameters parameters);

  LearningAlgorithm createLearner(int counter, Parameters parameters);
}
