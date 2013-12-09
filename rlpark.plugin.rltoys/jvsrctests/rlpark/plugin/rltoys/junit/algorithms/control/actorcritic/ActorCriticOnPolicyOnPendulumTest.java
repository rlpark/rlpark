package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic;

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
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.experiments.testing.control.PendulumOnPolicyLearning;
import rlpark.plugin.rltoys.experiments.testing.control.PendulumOnPolicyLearning.ControlFactory;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

public class ActorCriticOnPolicyOnPendulumTest {
  public abstract class ActorCriticFactory implements ControlFactory {
    @Override
    public ControlLearner create(SwingPendulum problem, int vectorSize, double vectorNorm) {
      PolicyDistribution policyDistribution = new NormalDistributionScaled(new Random(0), 0.0, 1.0);
      return create(vectorSize, vectorNorm, policyDistribution);
    }

    protected abstract ControlLearner create(int vectorSize, double vectorNorm, PolicyDistribution policyDistribution);
  }

  @Test
  public void testRandom() {
    Assert.assertTrue(PendulumOnPolicyLearning.evaluate(new ActorCriticFactory() {
      @Override
      public ControlLearner create(int vectorSize, double vectorNorm, PolicyDistribution policyDistribution) {
        TD critic = new TD(0.0, 0.0, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.0, vectorSize);
        return new ActorCritic(critic, actor);
      }
    }) < 0.0);
  }

  @Test
  public void testActorCritic() {
    Assert.assertTrue(PendulumOnPolicyLearning.evaluate(new ActorCriticFactory() {
      @Override
      public ControlLearner create(int vectorSize, double vectorNorm, PolicyDistribution policyDistribution) {
        TD critic = new TD(1.0, 0.5 / vectorNorm, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.05 / vectorNorm, vectorSize);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }) > .65);
  }

  @Test
  public void testActorCriticWithEligiblity() {
    Assert.assertTrue(PendulumOnPolicyLearning.evaluate(new ActorCriticFactory() {
      @Override
      public ControlLearner create(int vectorSize, double vectorNorm, PolicyDistribution policyDistribution) {
        double lambda = .5;
        Traces traces = new ATraces();
        TD critic = new TDLambda(lambda, 1.0, 0.1 / vectorNorm, vectorSize, traces);
        Actor actor = new ActorLambda(lambda, 1.0, policyDistribution, 0.05 / vectorNorm, vectorSize, traces);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }) > .75);
  }
}
