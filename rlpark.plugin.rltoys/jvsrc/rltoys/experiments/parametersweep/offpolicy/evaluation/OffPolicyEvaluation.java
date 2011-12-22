package rltoys.experiments.parametersweep.offpolicy.evaluation;

import java.io.Serializable;

import rltoys.algorithms.representations.projectors.RepresentationFactory;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.offpolicy.OffPolicyAgentEvaluable;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

public interface OffPolicyEvaluation extends Serializable {
  AgentEvaluator connectEvaluator(int counter, Runner behaviourRunner, OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentEvaluable learningAgent, Parameters parameters);

  int nbRewardCheckpoint();
}
