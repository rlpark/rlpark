package rlpark.plugin.rltoys.experiments.testing.control;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.experiments.helpers.Evaluations;
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
    return Evaluations.runEpisode(problem, control, tileCoders, 50, 5000);
  }
}
