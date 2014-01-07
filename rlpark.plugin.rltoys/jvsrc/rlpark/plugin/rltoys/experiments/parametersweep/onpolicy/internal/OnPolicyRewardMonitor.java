package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.runners.Runner;

public interface OnPolicyRewardMonitor extends PerformanceEvaluator {
  void connect(Runner runner);
}
