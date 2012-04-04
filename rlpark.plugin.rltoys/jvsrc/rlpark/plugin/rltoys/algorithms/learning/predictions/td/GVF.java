package rlpark.plugin.rltoys.algorithms.learning.predictions.td;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface GVF extends OffPolicyTD {
  double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1, double z_tp1);
}
