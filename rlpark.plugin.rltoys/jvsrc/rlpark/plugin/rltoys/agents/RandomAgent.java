package rlpark.plugin.rltoys.agents;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.acting.RandomPolicy;
import rlpark.plugin.rltoys.envio.actions.Action;

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
