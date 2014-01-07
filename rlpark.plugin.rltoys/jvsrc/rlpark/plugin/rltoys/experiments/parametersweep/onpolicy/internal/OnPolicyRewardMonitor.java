package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;

public interface OnPolicyRewardMonitor extends PerformanceEvaluator {
  void connect(AbstractRunner runner);
}
