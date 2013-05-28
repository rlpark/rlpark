package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.labels.Labeled;

public interface OffPolicyAgentFactory extends Serializable, Labeled {
  OffPolicyAgent createAgent(long seed, RLProblem problem, Parameters parameters,
      RepresentationFactory representationFactory);
}
