package rltoys.algorithms.learning.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rltoys.algorithms.learning.predictions.td.TD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.environments.nostate.NoStateProblem;
import rltoys.environments.nostate.NoStateProblem.NoStateRewardFunction;
import rltoys.experiments.continuousaction.NoStateExperiment;

public class ActorCriticOnPolicyOnStateTest {
  static final double gamma = 0.9;
  static final double RewardRequired = 0.6;
  public static final NoStateRewardFunction rewardFunction = new NoStateProblem.NormalReward(0.2, 0.5);

  private ActorCritic createActorCritic(PolicyDistribution policyDistribution, int nbFeatures) {
    TD critic = new TD(gamma, 0.1 / nbFeatures, nbFeatures);
    Actor actor = new Actor(policyDistribution, 0.01 / nbFeatures, nbFeatures);
    return new ActorCritic(critic, actor);
  }

  private void checkDistribution(NormalDistribution policy) {
    ActorCritic actorCritic = createActorCritic(policy, 1);
    RLProblem environment = new NoStateProblem(rewardFunction);
    double discReward = NoStateExperiment.evaluateActorCritic(10000, environment, actorCritic);
    Assert.assertTrue(discReward > RewardRequired);
  }

  @Test
  public void testNormalDistribution() {
    checkDistribution(new NormalDistribution(new Random(0), 0.5, 1.0));
  }

  @Test
  public void testNormalDistributionMeanAdjusted() {
    checkDistribution(new NormalDistributionSkewed(new Random(0), 0.5, 1.0));
  }

  @Test
  public void testNormalDistributionWithEligibility() {
    double lambda = 0.2;
    TD critic = new TDLambda(lambda, gamma, 0.5 / 1, 1);
    Actor actor = new ActorLambda(lambda, new NormalDistribution(new Random(0), 0.5, 1.0), 0.1, 1);
    ActorCritic actorCritic = new ActorCritic(critic, actor);
    RLProblem environment = new NoStateProblem(rewardFunction);
    double discReward = NoStateExperiment.evaluateActorCritic(1000, environment, actorCritic);
    Assert.assertTrue(discReward > RewardRequired);
  }
}
