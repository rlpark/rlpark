package rlpark.plugin.rltoys.agents;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.Control;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

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