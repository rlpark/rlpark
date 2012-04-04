package rlpark.plugin.rltoys.agents;

import rlpark.plugin.rltoys.envio.actions.Action;

public interface Agent {
  Action getAtp1(double[] obs);
}
