package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import java.io.Serializable;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

public interface OffPolicyEvaluation extends Serializable {
  AgentEvaluator connectEvaluator(int counter, Runner behaviourRunner, OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentEvaluable learningAgent, Parameters parameters);

  int nbRewardCheckpoint();
}
