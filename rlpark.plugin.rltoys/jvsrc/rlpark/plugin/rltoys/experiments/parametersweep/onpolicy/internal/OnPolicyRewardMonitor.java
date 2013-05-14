package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;

public interface OnPolicyRewardMonitor extends PerformanceEvaluator {
  void connect(Runner runner);
}
