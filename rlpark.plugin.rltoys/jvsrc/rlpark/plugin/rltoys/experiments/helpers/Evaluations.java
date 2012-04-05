package rlpark.plugin.rltoys.experiments.helpers;

import rlpark.plugin.rltoys.agents.rl.LearnerAgent;
import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.problems.RLProblem;

public class Evaluations {
  static public double runEpisode(RLProblem problem, RLAgent agent, int nbEpisodes, int nbTimeSteps) {
    Runner runner = new Runner(problem, agent, nbEpisodes, nbTimeSteps);
    runner.run();
    return runner.runnerEvent().episodeReward / runner.runnerEvent().step.time;
  }

  public static double runEpisode(RLProblem problem, ControlLearner control, int nbEpisodes, int nbTimeSteps) {
    RLAgent agent = new LearnerAgent(control);
    return runEpisode(problem, agent, nbEpisodes, nbTimeSteps);
  }

  public static double runEpisode(RLProblem problem, ControlLearner control, Projector projector, int nbEpisodes,
      int nbTimeSteps) {
    RLAgent agent = new LearnerAgentFA(control, projector);
    return runEpisode(problem, agent, nbEpisodes, nbTimeSteps);
  }
}
