package rlpark.plugin.rltoys.algorithms.learning.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.acting.PolicyDistribution;
import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.experiments.continuousaction.NoStateExperiment;
import rlpark.plugin.rltoys.problems.nostate.NoStateProblem;
import rlpark.plugin.rltoys.problems.nostate.NoStateProblem.NoStateRewardFunction;

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
    Actor actor = new ActorLambda(lambda, gamma, new NormalDistribution(new Random(0), 0.5, 1.0), 0.1, 1);
    ActorCritic actorCritic = new ActorCritic(critic, actor);
    RLProblem environment = new NoStateProblem(rewardFunction);
    double discReward = NoStateExperiment.evaluateActorCritic(1000, environment, actorCritic);
    Assert.assertTrue(discReward > RewardRequired);
  }
}
