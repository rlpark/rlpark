package rlpark.plugin.rltoys.envio.rl;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;

public interface RLAgent extends Serializable {
  Action getAtp1(TRStep step);
}
