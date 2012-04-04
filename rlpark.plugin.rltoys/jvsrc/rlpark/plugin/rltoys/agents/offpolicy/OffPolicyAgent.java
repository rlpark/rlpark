package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.algorithms.learning.control.offpolicy.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;

public interface OffPolicyAgent extends RLAgent {
  Policy behaviour();

  OffPolicyLearner learner();
}
