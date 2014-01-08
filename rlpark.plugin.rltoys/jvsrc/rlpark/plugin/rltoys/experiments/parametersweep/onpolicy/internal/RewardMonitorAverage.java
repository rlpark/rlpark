package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitorAverage extends AbstractPerformanceMonitor implements OnPolicyRewardMonitor {
  public RewardMonitorAverage(String prefix, int nbBins, int nbTimeSteps) {
    super(prefix, "Reward", createStartingPoints(nbBins, nbTimeSteps));
  }

  @Override
  public void connect(final AbstractRunner runner) {
    runner.onTimeStep.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @SuppressWarnings("synthetic-access")
      @Override
      public void listen(AbstractRunner.RunnerEvent runnerEvent) {
        if (runnerEvent.nbTotalTimeSteps - 1 == 0)
          return;
        registerMeasurement(runnerEvent.nbTotalTimeSteps - 1, runnerEvent.step.r_tp1);
      }
    });
  }

  @Override
  protected double worstValue() {
    return -Float.MAX_VALUE;
  }
}
