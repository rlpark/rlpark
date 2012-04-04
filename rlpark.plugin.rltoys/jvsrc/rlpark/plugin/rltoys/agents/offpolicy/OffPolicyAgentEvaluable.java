package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.agents.RLAgent;

public interface OffPolicyAgentEvaluable extends OffPolicyAgent {
  RLAgent createEvaluatedAgent();
}
