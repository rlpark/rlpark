package rltoys.environments.envio.agents;

import java.util.Random;

import rltoys.algorithms.representations.acting.RandomPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.Agent;

public class RandomAgent implements Agent {
  private final RandomPolicy policy;

  public RandomAgent(Random random, Action[] actions) {
    policy = new RandomPolicy(random, actions);
  }

  @Override
  public Action getAtp1(double[] obs) {
    return policy.decide(null);
  }

  public RandomPolicy policy() {
    return policy;
  }
}
