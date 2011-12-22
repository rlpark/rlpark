package rltoys.environments.envio.agents;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.control.Control;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.states.Projector;

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