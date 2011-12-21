package rltoys.experiments.parametersweep.reinforcementlearning;

import rltoys.experiments.parametersweep.parameters.Parameters;


public interface AgentEvaluator {
  void worstResultUntilEnd();

  void putResult(Parameters parameters);
}