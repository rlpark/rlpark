package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractRewardMonitor;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitorAverage extends AbstractRewardMonitor implements OnPolicyRewardMonitor {
  public RewardMonitorAverage(int nbRewardBins, int nbTimeSteps) {
    this("", nbRewardBins, nbTimeSteps);
  }

  public RewardMonitorAverage(String prefix, int nbRewardBins, int nbTimeSteps) {
    super(prefix, createStartingPoints(nbRewardBins, nbTimeSteps));
  }

  @Override
  public void connect(Runner runner) {
    runner.onTimeStep.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent runnerEvent) {
        if (runnerEvent.step.time == 0)
          return;
        registerMeasurement(runnerEvent.step.time - 1, runnerEvent.step.r_tp1);
      }
    });
  }
}
