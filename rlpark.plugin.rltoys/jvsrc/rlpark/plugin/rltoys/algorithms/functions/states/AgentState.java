package rlpark.plugin.rltoys.algorithms.functions.states;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface AgentState extends Serializable {
  RealVector update(Action a_t, Observation o_tp1);

  double stateNorm();

  int stateSize();
}
