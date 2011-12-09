package rltoys.experiments.parametersweep.offpolicy.evaluation;

import java.io.Serializable;

import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;

public interface OffPolicyEvaluation extends Serializable {
  AgentEvaluator connectEvaluator(int counter, Runner behaviourRunner, OffPolicyProblemFactory environmentFactory,
      ProjectorFactory projectorFactory, OffPolicyLearner learner, Parameters parameters);

  int nbRewardCheckpoint();
}
