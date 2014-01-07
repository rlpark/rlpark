package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistributionSkewed;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.VectorNull;
import rlpark.plugin.rltoys.problems.RLProblem;
import rlpark.plugin.rltoys.problems.nostate.NoStateProblem;
import rlpark.plugin.rltoys.problems.nostate.NoStateProblem.NoStateRewardFunction;

public class ActorCriticOnPolicyOnStateTest {
  @SuppressWarnings("serial")
  static final private Projector projector = new Projector() {
    final private PVector projected = new PVector(1.0);

    @Override
    public int vectorSize() {
      return 1;
    }

    @Override
    public double vectorNorm() {
      return 1;
    }

    @Override
    public RealVector project(double[] obs) {
      return obs != null ? projected : new VectorNull(1);
    }
  };

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
    RLProblem problem = new NoStateProblem(rewardFunction);
    double discReward = runEpisode(problem, actorCritic, projector, 1, 10000);
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
    RLProblem problem = new NoStateProblem(rewardFunction);
    double discReward = runEpisode(problem, actorCritic, projector, 1, 1000);
    Assert.assertTrue(discReward > RewardRequired);
  }

  static double runEpisode(RLProblem problem, RLAgent agent, int nbEpisodes, int nbTimeSteps) {
    Runner runner = new Runner(problem, agent, nbEpisodes, nbTimeSteps);
    runner.run();
    return runner.runnerEvent().episodeReward / runner.runnerEvent().step.time;
  }

  static double runEpisode(RLProblem problem, ControlLearner control, Projector projector, int nbEpisodes,
      int nbTimeSteps) {
    RLAgent agent = new LearnerAgentFA(control, projector);
    return runEpisode(problem, agent, nbEpisodes, nbTimeSteps);
  }
}
