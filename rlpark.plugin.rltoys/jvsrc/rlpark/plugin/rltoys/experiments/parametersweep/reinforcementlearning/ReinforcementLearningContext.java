package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.runners.Runner;

public interface ReinforcementLearningContext extends Context {
  Runner createRunner(int currentIndex, Parameters parameters);
}
