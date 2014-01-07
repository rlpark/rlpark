package rlpark.plugin.rltoys.experiments.runners;

import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.problems.RLProblem;

public class RunnerTimeSteps extends AbstractRunner {
  private static final long serialVersionUID = 2933423571699420939L;
  private final int nbTotalTimeStep;

  public RunnerTimeSteps(RLProblem problem, RLAgent agent) {
    this(problem, agent, -1, -1);
  }

  public RunnerTimeSteps(RLProblem environment, RLAgent agent, int maxEpisodeTimeSteps, int nbTotalTimeStep) {
    super(environment, agent, maxEpisodeTimeSteps);
    this.nbTotalTimeStep = nbTotalTimeStep;
  }

  @Override
  public void run() {
    while (runnerEvent.nbTotalTimeSteps < this.nbTotalTimeStep)
      step();
  }
}
