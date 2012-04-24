package rlpark.plugin.rltoys.experiments.testing.control;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistributionScaled;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.experiments.helpers.Evaluations;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

public class ActorCriticOnPendulum {
  public interface ActorCriticFactory {
    ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution);
  }

  public static double evaluate(ActorCriticFactory actorCriticFactory) {
    SwingPendulum problem = new SwingPendulum(null);
    TileCoders tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(10, 10);
    tileCoders.includeActiveFeature();
    int vectorSize = tileCoders.vectorSize();
    int nbActive = (int) tileCoders.vectorNorm();
    PolicyDistribution policyDistribution = new NormalDistributionScaled(new Random(0), 0.0, 1.0);
    ControlLearner control = actorCriticFactory.createActorCritic(vectorSize, nbActive, policyDistribution);
    return Evaluations.runEpisode(problem, control, tileCoders, 50, 5000);
  }
}
