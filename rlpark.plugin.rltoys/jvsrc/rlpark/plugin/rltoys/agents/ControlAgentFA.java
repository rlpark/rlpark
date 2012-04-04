package rlpark.plugin.rltoys.agents;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.Control;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.envio.states.Projector;

public class ControlAgentFA implements RLAgent {
  private static final long serialVersionUID = 1863728076381568361L;
  private final Control control;
  private final Projector projector;

  public ControlAgentFA(Projector projector, Control control) {
    this.projector = projector;
    this.control = control;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeEnding())
      return null;
    return control.proposeAction(projector.project(step.o_tp1));
  }
}