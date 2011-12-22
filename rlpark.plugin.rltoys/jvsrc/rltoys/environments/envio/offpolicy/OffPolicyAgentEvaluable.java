package rltoys.environments.envio.offpolicy;

import rltoys.environments.envio.RLAgent;

public interface OffPolicyAgentEvaluable extends OffPolicyAgent {
  RLAgent createEvaluatedAgent();
}
