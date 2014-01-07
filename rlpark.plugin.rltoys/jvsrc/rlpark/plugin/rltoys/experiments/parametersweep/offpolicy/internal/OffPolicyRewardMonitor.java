package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractEpisodeRewardMonitor;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;

public class OffPolicyRewardMonitor extends AbstractEpisodeRewardMonitor {
  private final Runner runner;
  private int nextEvaluationIndex = 0;
  private final int nbEpisodePerEvaluation;

  public OffPolicyRewardMonitor(Runner runner, int nbLearnerEvaluation, int nbTotalBehaviourLength,
      int nbEpisodePerEvaluation) {
    super("Target", createStartingPoints(nbLearnerEvaluation, nbTotalBehaviourLength));
    this.runner = runner;
    this.nbEpisodePerEvaluation = nbEpisodePerEvaluation;
  }

  static protected int[] createStartingPoints(int nbLearnerEvaluation, int nbTotalBehaviourLength) {
    int[] starts = new int[nbLearnerEvaluation];
    double binSize = (double) nbTotalBehaviourLength / (nbLearnerEvaluation - 1);
    for (int i = 0; i < starts.length; i++)
      starts[i] = (int) (i * binSize);
    starts[starts.length - 1] = nbTotalBehaviourLength - 1;
    return starts;
  }

  public void runEvaluationIFN(int currentIndex) {
    if (nextEvaluationIndex >= starts.length || starts[nextEvaluationIndex] > currentIndex)
      return;
    for (int i = 0; i < nbEpisodePerEvaluation; i++) {
      runner.runEpisode();
      AbstractRunner.RunnerEvent runnerEvent = runner.runnerEvent();
      registerMeasurement(currentIndex, runnerEvent.episodeReward, runnerEvent.step.time);
    }
    nextEvaluationIndex++;
  }
}
