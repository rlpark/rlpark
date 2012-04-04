package rlpark.plugin.rltoys.agents;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.TRStep;

public interface RLAgent extends Serializable {
  Action getAtp1(TRStep step);
}
