package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class Policies {
  public static Action decide(Policy policy, RealVector x) {
    policy.update(x);
    return policy.sampleAction();
  }

  @SuppressWarnings("serial")
  public static RLAgent toAgent(final Policy policy) {
    return new RLAgent() {
      @Override
      public Action getAtp1(TRStep step) {
        policy.update(new PVector(step.o_tp1));
        return policy.sampleAction();
      }
    };
  }

  @SuppressWarnings("serial")
  public static RLAgent toAgentFA(final Policy policy, final Projector projector) {
    return new RLAgent() {
      @Override
      public Action getAtp1(TRStep step) {
        policy.update(projector.project(step.o_tp1));
        return policy.sampleAction();
      }
    };
  }
}
