package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;

public interface OffPolicyAgent extends RLAgent {
  Policy behaviour();

  OffPolicyLearner learner();
}
