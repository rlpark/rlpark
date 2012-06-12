package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface ActorOffPolicy {
  void update(double rho_t, RealVector x_t, Action a_t, double delta);

  PolicyDistribution policy();

  Action proposeAction(RealVector s);

  PVector[] actorParameters();
}
