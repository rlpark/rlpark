package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.AverageRewardActorCritic;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.experiments.testing.ActorCriticOnPendulum;
import rlpark.plugin.rltoys.experiments.testing.ActorCriticOnPendulum.ActorCriticFactory;

public class ActorCriticOnPolicyOnPendulumTest {
  @Test
  public void testRandom() {
    Assert.assertTrue(ActorCriticOnPendulum.evaluate(new ActorCriticFactory() {
      @Override
      public ActorCritic createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(0.0, 0.0, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.0, vectorSize);
        return new ActorCritic(critic, actor);
      }
    }) < 0.0);
  }

  @Test
  public void testActorCritic() {
    Assert.assertTrue(ActorCriticOnPendulum.evaluate(new ActorCriticFactory() {
      @Override
      public ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        TD critic = new TD(1.0, 0.5 / nbActive, vectorSize);
        Actor actor = new Actor(policyDistribution, 0.05 / nbActive, vectorSize);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }) > .75);
  }

  @Test
  public void testActorCriticWithEligiblity() {
    Assert.assertTrue(ActorCriticOnPendulum.evaluate(new ActorCriticFactory() {
      @Override
      public ControlLearner createActorCritic(int vectorSize, int nbActive, PolicyDistribution policyDistribution) {
        double lambda = .5;
        Traces traces = new ATraces();
        TD critic = new TDLambda(lambda, 1.0, 0.1 / nbActive, vectorSize, traces);
        Actor actor = new ActorLambda(lambda, 1.0, policyDistribution, 0.05 / nbActive, vectorSize, traces);
        return new AverageRewardActorCritic(0.01, critic, actor);
      }
    }) > .75);
  }
}
