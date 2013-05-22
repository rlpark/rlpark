package rlpark.plugin.rltoys.agents.rl;

import rlpark.plugin.rltoys.algorithms.control.Control;
import rlpark.plugin.rltoys.algorithms.control.acting.ControlPolicyAdapter;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;

public class ControlAgentFA implements RLAgent {
  private static final long serialVersionUID = 1863728076381568361L;
  private final Control control;
  private final Projector projector;

  public ControlAgentFA(Policy policy, Projector projector) {
    this(new ControlPolicyAdapter(policy), projector);
  }

  public ControlAgentFA(Control control, Projector projector) {
    this.projector = projector;
    this.control = control;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeEnding())
      return null;
    return control.proposeAction(projector.project(step.o_tp1));
  }

  public Control control() {
    return control;
  }

  public Projector projector() {
    return projector;
  }
}