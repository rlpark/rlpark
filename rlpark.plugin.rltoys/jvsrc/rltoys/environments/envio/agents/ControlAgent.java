package rltoys.environments.envio.agents;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.control.Control;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.implementations.PVector;

public class ControlAgent implements RLAgent {
  private static final long serialVersionUID = 4670115173783709550L;
  private final Control control;

  public ControlAgent(Control control) {
    this.control = control;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeEnding())
      return null;
    return control.proposeAction(new PVector(step.o_tp1));
  }
}