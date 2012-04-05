package rlpark.plugin.rltoys.testing.algorithms.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.AverageRewardActorCritic;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistributionScaled;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.experiments.helpers.Evaluations;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

public class ActorCriticOnPolicyOnPendulumTest {
  public interface ActorCriticFactory {
    ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution);
  }

  public static boolean checkActorCriticOnPendulum(ActorCriticFactory actorCriticFactory) {
    SwingPendulum problem = new SwingPendulum(null);
    TileCoders tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(10, 10);
    tileCoders.includeActiveFeature();
    int vectorSize = tileCoders.vectorSize();
    int nbActive = (int) tileCoders.vectorNorm();
    PolicyDistribution policyDistribution = new NormalDistributionScaled(new Random(0), 0.0, 1.0);
    ControlLearner control = actorCriticFactory.createActorCritic(vectorSize, nbActive, policyDistribution);
    return Evaluations.runEpisode(problem, control, tileCoders, 50, 5000) > .75;
  }

  @Test
  public void testRandom() {
    Assert.assertFalse(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ActorCritic createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(0.0, 0.0, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.0, vectorSize);
        return new ActorCritic(critic, actor);
      }
    }));
  }

  @Test
  public void testActorCritic() {
    Assert.assertTrue(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(1.0, 0.5 / nbActive, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.05 / nbActive, vectorSize);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }));
  }

  @Test
  public void testActorCriticWithEligiblity() {
    Assert.assertTrue(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        double lambda = .5;
        Traces traces = new ATraces();
        TD critic = new TDLambda(lambda, 1.0, 0.1 / nbActive, vectorSize, traces);
        Actor actor = new ActorLambda(lambda, 1.0, policyDistribution, 0.05 / nbActive, vectorSize, traces);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }));
  }
}
