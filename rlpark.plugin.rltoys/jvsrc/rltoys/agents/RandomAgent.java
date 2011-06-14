package rltoys.agents;

import java.util.Random;

import rltoys.algorithms.representations.acting.RandomPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.TStep;

public class RandomAgent implements Agent {
  private static final long serialVersionUID = 5585016330810380595L;
  private final RandomPolicy policy;

  public RandomAgent(Random random, Action[] actions) {
    policy = new RandomPolicy(random, actions);
  }

  @Override
  public Action getAtp1(TStep step) {
    return policy.decide(null);
  }

  public RandomPolicy policy() {
    return policy;
  }
}