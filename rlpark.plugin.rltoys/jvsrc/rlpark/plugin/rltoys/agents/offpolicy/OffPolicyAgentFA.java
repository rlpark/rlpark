package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.agents.ControlAgentFA;
import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.algorithms.learning.control.offpolicy.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class OffPolicyAgentFA implements OffPolicyAgentEvaluable {
  private static final long serialVersionUID = 3773760092579439924L;
  private final Projector projector;
  private RealVector x_t;
  private final OffPolicyLearner learner;
  private final Policy behaviour;

  public OffPolicyAgentFA(Projector projector, Policy behaviour, OffPolicyLearner learner) {
    this.projector = projector;
    this.learner = learner;
    this.behaviour = behaviour;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeStarting())
      x_t = null;
    RealVector x_tp1 = projector.project(step.o_tp1);
    Action a_tp1 = behaviour.decide(x_tp1);
    learner.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  @Override
  public OffPolicyLearner learner() {
    return learner;
  }

  @Override
  public Policy behaviour() {
    return behaviour;
  }

  @Override
  public RLAgent createEvaluatedAgent() {
    return new ControlAgentFA(projector, learner);
  }
}
