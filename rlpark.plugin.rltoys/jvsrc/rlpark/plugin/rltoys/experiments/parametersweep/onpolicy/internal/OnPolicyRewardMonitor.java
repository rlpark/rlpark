package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;

public interface OnPolicyRewardMonitor extends AgentEvaluator {
  void connect(Runner runner);
}
