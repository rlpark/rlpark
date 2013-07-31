package rlpark.plugin.rltoys.algorithms.predictions.td;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
@SuppressWarnings("restriction")
public class TD implements OnPolicyTD {
  private static final long serialVersionUID = -3640476464100200081L;
  protected double alpha_v;
  protected double gamma;
  @Monitor(level = 4)
  final public PVector v;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double delta_t;
  protected double v_t;

  public TD(double alpha_v, int nbFeatures) {
    this(Double.NaN, alpha_v, nbFeatures);
  }

  public TD(double gamma, double alpha_v, int nbFeatures) {
    this.alpha_v = alpha_v;
    this.gamma = gamma;
    v = new PVector(nbFeatures);
  }

  protected double initEpisode() {
    v_t = 0;
    delta_t = 0;
    return delta_t;
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return update(x_t, x_tp1, r_tp1, gamma);
  }

  public double update(RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    v.addToSelf(alpha_v * delta_t, x_t);
    return delta_t;
  }

  @Override
  public double predict(RealVector phi) {
    return v.dotProduct(phi);
  }

  public double gamma() {
    return gamma;
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
  }

  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public double prediction() {
    return v_t;
  }

  public double alpha() {
    return alpha_v;
  }
}
