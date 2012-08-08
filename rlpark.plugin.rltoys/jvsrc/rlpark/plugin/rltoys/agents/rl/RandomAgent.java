package rlpark.plugin.rltoys.agents.rl;

import java.util.Random;

import rlpark.plugin.rltoys.agents.Agent;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.RandomPolicy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;

public class RandomAgent implements Agent, RLAgent {
  private static final long serialVersionUID = 1222156748593819208L;
  private final RandomPolicy policy;

  public RandomAgent(Random random, Action[] actions) {
    policy = new RandomPolicy(random, actions);
  }

  @Override
  public Action getAtp1(double[] obs) {
    return Policies.decide(policy, null);
  }

  @Override
  public Action getAtp1(TRStep step) {
    return Policies.decide(policy, null);
  }

  public RandomPolicy policy() {
    return policy;
  }
}
