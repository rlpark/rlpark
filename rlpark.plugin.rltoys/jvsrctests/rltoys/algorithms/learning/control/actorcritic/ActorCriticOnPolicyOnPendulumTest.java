package rltoys.algorithms.learning.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rltoys.algorithms.learning.predictions.td.TD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.pendulum.SwingPendulum;
import rltoys.experiments.continuousaction.SwingPendulumExperiment;

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
    PolicyDistribution policyDistribution = new NormalDistributionSkewed(new Random(0), 0.0, 1.0, 0.1);
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
        Actor actor = new ActorLambda(lambda, policyDistribution, 0.01 / nbActive, vectorSize, traces);
        return new ActorCritic(critic, actor);
      }
    }));
  }
}
