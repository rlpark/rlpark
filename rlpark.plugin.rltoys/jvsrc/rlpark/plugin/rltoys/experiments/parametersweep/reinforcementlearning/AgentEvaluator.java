package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;


public interface AgentEvaluator {
  void worstResultUntilEnd();

  void putResult(Parameters parameters);
}