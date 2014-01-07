package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;

public interface OffPolicyEvaluationContext extends ReinforcementLearningContext {
  PerformanceEvaluator connectBehaviourRewardMonitor(AbstractRunner runner, Parameters parameters);

  PerformanceEvaluator connectTargetRewardMonitor(int counter, AbstractRunner runner, Parameters parameters);
}
