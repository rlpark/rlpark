package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public interface OnPolicyEvaluationContext extends ReinforcementLearningContext {
  OnPolicyRewardMonitor createRewardMonitor(Parameters parameters);
}
