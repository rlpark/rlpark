package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractEpisodeRewardMonitor;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitorEpisode extends AbstractEpisodeRewardMonitor implements OnPolicyRewardMonitor {
  public RewardMonitorEpisode(String prefix, int nbBins, int nbEpisode) {
    super(prefix, createStartingPoints(nbBins, nbEpisode));
  }

  @Override
  public void connect(AbstractRunner runner) {
    runner.onEpisodeEnd.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        registerMeasurement(eventInfo.nbEpisodeDone, eventInfo.episodeReward, eventInfo.step.time);
      }
    });
  }
}
