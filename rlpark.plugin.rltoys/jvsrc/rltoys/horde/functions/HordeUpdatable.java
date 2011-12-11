package rltoys.horde.functions;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.Observation;
import rltoys.math.vector.RealVector;

public interface HordeUpdatable {
  void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1);
}
