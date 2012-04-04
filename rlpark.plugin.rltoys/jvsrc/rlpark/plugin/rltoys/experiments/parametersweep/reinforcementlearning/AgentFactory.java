package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;

import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface AgentFactory extends Labeled, Serializable {
  public RLAgent createAgent(RLProblem problem, Parameters parameters, long seed);
}
