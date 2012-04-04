package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public interface ReinforcementLearningContext extends Context {
  Runner createRunner(int currentIndex, Parameters parameters);
}
