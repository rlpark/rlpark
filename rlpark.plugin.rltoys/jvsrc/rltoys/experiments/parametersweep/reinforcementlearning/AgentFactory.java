package rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface AgentFactory extends Labeled, Serializable {
  public RLAgent createAgent(RLProblem problem, Parameters parameters, long seed);
}
