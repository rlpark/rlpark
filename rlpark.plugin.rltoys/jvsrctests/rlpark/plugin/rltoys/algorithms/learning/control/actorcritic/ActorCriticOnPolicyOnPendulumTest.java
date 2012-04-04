package rlpark.plugin.rltoys.algorithms.learning.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.acting.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.representations.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.representations.traces.Traces;
import rlpark.plugin.rltoys.envio.control.ControlLearner;
import rlpark.plugin.rltoys.experiments.continuousaction.SwingPendulumExperiment;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

public class ActorCriticOnPolicyOnPendulumTest {
  public interface ActorCriticFactory {
    ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution);
  }

  static private final double gamma = 0.9;

  public static boolean checkActorCriticOnPendulum(ActorCriticFactory actorCriticFactory) {
    SwingPendulum problem = new SwingPendulum(new Random(0));
    TileCoders tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(8, 8);
    tileCoders.includeActiveFeature();
    int vectorSize = tileCoders.vectorSize();
    int nbActive = (int) tileCoders.vectorNorm();
    PolicyDistribution policyDistribution = new NormalDistributionSkewed(new Random(0), 0.0, 1.0);
    ControlLearner control = actorCriticFactory.createActorCritic(vectorSize, nbActive, policyDistribution);
    return SwingPendulumExperiment.checkActorCritic(problem, control, tileCoders, 50);
  }

  @Test
  public void testRandom() {
    Assert.assertFalse(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ActorCritic createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(gamma, 0.0, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.0, vectorSize);
        return new ActorCritic(critic, actor);
      }
    }));
  }

  @Test
  public void testActorCritic() {
    Assert.assertTrue(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ActorCritic createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(gamma, 0.1 / nbActive, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.01 / nbActive, vectorSize);
        return new ActorCritic(critic, actor);
      }
    }));
  }

  @Test
  public void testActorCriticWithEligiblity() {
    Assert.assertTrue(checkActorCriticOnPendulum(new ActorCriticFactory() {
      @Override
      public ActorCritic createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        double lambda = 0.7;
        Traces traces = new AMaxTraces();
        TD critic = new TDLambda(lambda, gamma, 0.1 / nbActive, vectorSize, traces);
        Actor actor = new ActorLambda(lambda, gamma, policyDistribution, 0.01 / nbActive, vectorSize, traces);
        return new ActorCritic(critic, actor);
      }
    }));
  }
}
