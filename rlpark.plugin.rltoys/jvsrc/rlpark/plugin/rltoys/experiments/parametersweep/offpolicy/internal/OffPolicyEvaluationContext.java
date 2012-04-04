package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public interface OffPolicyEvaluationContext extends ReinforcementLearningContext {
  AgentEvaluator connectBehaviourRewardMonitor(Runner runner, Parameters parameters);

  AgentEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters);
}
