package rltoys.environments.envio.offpolicy;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.environments.envio.RLAgent;

public interface OffPolicyAgent extends RLAgent {
  Policy behaviour();

  OffPolicyLearner learner();
}
