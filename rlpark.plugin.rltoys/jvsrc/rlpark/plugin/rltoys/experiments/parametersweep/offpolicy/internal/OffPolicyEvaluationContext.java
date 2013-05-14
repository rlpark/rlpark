package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public interface OffPolicyEvaluationContext extends ReinforcementLearningContext {
  PerformanceEvaluator connectBehaviourRewardMonitor(Runner runner, Parameters parameters);

  PerformanceEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters);
}
