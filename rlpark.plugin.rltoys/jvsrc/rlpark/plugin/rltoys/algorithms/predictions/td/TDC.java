package rlpark.plugin.rltoys.algorithms.predictions.td;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class TDC extends TD {
  private static final long serialVersionUID = 7305877522126081130L;
  @Monitor(level = 4)
  protected final PVector w;
  public final double alpha_w;

  public TDC(double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    super(gamma, alpha_v, nbFeatures);
    w = new PVector(nbFeatures);
    this.alpha_w = alpha_w;
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    RealVector tdCorrection = (x_tp1 != null ? x_tp1.mapMultiply(x_t.dotProduct(w)).mapMultiply(alpha_v * gamma_tp1)
        : new PVector(x_t.getDimension()));
    v.addToSelf(x_t.mapMultiply(alpha_v * delta_t).subtract(tdCorrection));
    w.addToSelf(x_t.mapMultiply(alpha_w * (delta_t - x_t.dotProduct(w))));
    return delta_t;
  }

  @Override
  public void resetWeight(int index) {
    super.resetWeight(index);
    w.data[index] = 0;
  }

  public PVector secondaryWeights() {
    return w;
  }
}
