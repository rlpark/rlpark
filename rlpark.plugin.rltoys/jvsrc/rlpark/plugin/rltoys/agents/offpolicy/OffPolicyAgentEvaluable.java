package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.envio.rl.RLAgent;

public interface OffPolicyAgentEvaluable extends OffPolicyAgent {
  RLAgent createEvaluatedAgent();
}
