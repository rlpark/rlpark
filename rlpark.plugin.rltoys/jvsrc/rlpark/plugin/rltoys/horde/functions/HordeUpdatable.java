package rlpark.plugin.rltoys.horde.functions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface HordeUpdatable {
  void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1);
}
