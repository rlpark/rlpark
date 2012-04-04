package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import java.io.Serializable;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.algorithms.representations.projectors.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

public interface OffPolicyEvaluation extends Serializable {
  AgentEvaluator connectEvaluator(int counter, Runner behaviourRunner, OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentEvaluable learningAgent, Parameters parameters);

  int nbRewardCheckpoint();
}
