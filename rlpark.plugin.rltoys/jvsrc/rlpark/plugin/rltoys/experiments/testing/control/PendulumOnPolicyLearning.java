package rlpark.plugin.rltoys.experiments.testing.control;

import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

public class PendulumOnPolicyLearning {
  public interface ControlFactory {
    ControlLearner create(SwingPendulum problem, int vectorSize, double vectorNorm);
  }

  public static double evaluate(ControlFactory controlFactory) {
    SwingPendulum problem = new SwingPendulum(null);
    TileCoders tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(10, 10);
    tileCoders.includeActiveFeature();
    ControlLearner control = controlFactory.create(problem, tileCoders.vectorSize(), tileCoders.vectorNorm());
    RLAgent agent = new LearnerAgentFA(control, tileCoders);
    Runner runner = new Runner(problem, agent, 50, 5000);
    runner.run();
    return runner.runnerEvent().episodeReward / runner.runnerEvent().step.time;
  }
}
