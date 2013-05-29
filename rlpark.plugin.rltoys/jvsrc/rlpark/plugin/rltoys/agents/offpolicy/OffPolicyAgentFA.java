package rlpark.plugin.rltoys.agents.offpolicy;

import rlpark.plugin.rltoys.agents.rl.ControlAgentFA;
import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
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
    Action a_tp1 = Policies.decide(behaviour, x_tp1);
    learner.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
    x_t = Vectors.bufferedCopy(x_tp1, x_t);
    return a_tp1;
  }

  public OffPolicyLearner learner() {
    return learner;
  }

  public Policy behaviour() {
    return behaviour;
  }

  @Override
  public RLAgent createEvaluatedAgent() {
    return new ControlAgentFA(learner, projector);
  }

  public Projector projector() {
    return projector;
  }
}
