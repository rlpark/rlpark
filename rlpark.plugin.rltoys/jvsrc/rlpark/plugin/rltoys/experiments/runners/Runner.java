package rlpark.plugin.rltoys.experiments.runners;

import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.problems.RLProblem;

public class Runner extends AbstractRunner {
  private static final long serialVersionUID = 2933423571699420939L;
  private final int nbEpisode;

  public Runner(RLProblem problem, RLAgent agent) {
    this(problem, agent, -1, -1);
  }

  public Runner(RLProblem environment, RLAgent agent, int nbEpisode, int maxEpisodeTimeSteps) {
    super(environment, agent, maxEpisodeTimeSteps);
    this.nbEpisode = nbEpisode;
  }

  @Override
  public void run() {
    for (int i = 0; i < nbEpisode; i++)
      runEpisode();
  }
}
