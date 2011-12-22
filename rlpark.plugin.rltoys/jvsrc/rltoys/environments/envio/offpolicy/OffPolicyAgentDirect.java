package rltoys.environments.envio.offpolicy;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.agents.ControlAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public class OffPolicyAgentDirect implements OffPolicyAgentEvaluable {
  private static final long serialVersionUID = -8255597969677460009L;
  private RealVector x_t;
  private final OffPolicyLearner learner;
  private final Policy behaviour;

  public OffPolicyAgentDirect(Policy behaviour, OffPolicyLearner learner) {
    this.learner = learner;
    this.behaviour = behaviour;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeStarting())
      x_t = null;
    RealVector x_tp1 = new PVector(step.o_tp1);
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
    return new ControlAgent(learner);
  }
}
