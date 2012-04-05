package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.helpers.Runner.RunnerEvent;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractEpisodeRewardMonitor;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitorEpisode extends AbstractEpisodeRewardMonitor implements OnPolicyRewardMonitor {
  public RewardMonitorEpisode(String prefix, int nbBins, int nbEpisode) {
    super(prefix, createStartingPoints(nbBins, nbEpisode));
  }

  @Override
  public void connect(Runner runner) {
    runner.onEpisodeEnd.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        registerMeasurement(eventInfo.episode, eventInfo.episodeReward, eventInfo.step.time);
      }
    });
  }
}
