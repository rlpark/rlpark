package rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.projectors.RepresentationFactory;
import rltoys.environments.envio.offpolicy.OffPolicyAgent;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface OffPolicyAgentFactory extends Serializable, Labeled {
  Policy createBehaviourPolicy(long seed, RLProblem problem);

  OffPolicyAgent createAgent(RLProblem problem, RepresentationFactory projectorFactory, Parameters parameters, Policy behaviourPolicy,
      long seed);
}
