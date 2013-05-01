package rlpark.plugin.rltoys.algorithms.predictions.td;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class HTD implements OnPolicyTD, GVF {
  private static final long serialVersionUID = 8687476023177671278L;
  protected double gamma;
  public double alpha_v;
  public double alpha_w;
  @Monitor(level = 4)
  public PVector v;
  @Monitor(level = 4)
  protected final PVector w;
  public double v_t;
  protected double delta_t;
  private double correction;
  private double ratio;
  private double rho_t;

  public HTD(double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    this.alpha_v = alpha_v;
    this.gamma = gamma;
    this.alpha_w = alpha_w;
    v = new PVector(nbFeatures);
    w = new PVector(nbFeatures);
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return update(1, 1, x_t, x_tp1, r_tp1);
  }

  @Override
  public double update(double pi_t, double b_t, RealVector x_t, RealVector x_tp1, double r_tp1) {
    return update(pi_t, b_t, x_t, x_tp1, r_tp1, gamma, 0);
  }

  @Override
  public double update(double pi_t, double b_t, RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1,
      double z_tp1) {
    if (x_t == null)
      return initEpisode();
    VectorPool pool = VectorPools.pool(x_t);
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + (1 - gamma_tp1) * z_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    correction = w.dotProduct(x_tp1);
    ratio = (pi_t - b_t) / b_t;
    rho_t = pi_t / b_t;
    v.addToSelf(alpha_v,
                pool.newVector(x_t).mapMultiplyToSelf(rho_t * delta_t)
                    .addToSelf(pool.newVector(x_tp1).mapMultiplyToSelf(gamma_tp1 * ratio * correction)));
    w.addToSelf(alpha_w,
                pool.newVector(x_t).mapMultiplyToSelf(rho_t * (delta_t - correction))
                    .addToSelf(pool.newVector(x_tp1).mapMultiplyToSelf(-gamma_tp1 * correction)));
    pool.releaseAll();
    return delta_t;
  }

  protected double initEpisode() {
    v_t = 0;
    delta_t = 0;
    return delta_t;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
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
  public PVector secondaryWeights() {
    return w;
  }


  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public double prediction() {
    return v_t;
  }
}
