package rlpark.plugin.rltoys.algorithms.control.gq;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.EligibilityTraceAlgorithm;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class GQ implements Predictor, LinearLearner, EligibilityTraceAlgorithm {
  private static final long serialVersionUID = -4971665888576276439L;
  @Monitor(level = 4)
  public final PVector v;
  protected double alpha_v;
  protected double alpha_w;
  protected double beta_tp1;
  protected double lambda_t;
  @Monitor(level = 4)
  protected final PVector w;
  protected final Traces e;
  protected double delta_t;

  public GQ(double alpha_v, double alpha_w, double beta, double lambda, int nbFeatures) {
    this(alpha_v, alpha_w, beta, lambda, nbFeatures, new ATraces());
  }

  public GQ(double alpha_v, double alpha_w, double beta, double lambda, int nbFeatures, Traces prototype) {
    this.alpha_v = alpha_v;
    this.alpha_w = alpha_w;
    beta_tp1 = beta;
    lambda_t = lambda;
    e = prototype.newTraces(nbFeatures);
    v = new PVector(nbFeatures);
    w = new PVector(nbFeatures);
  }

  protected double initEpisode() {
    e.clear();
    return 0.0;
  }

  public double update(RealVector x_t, double rho_t, double r_tp1, RealVector x_bar_tp1, double z_tp1) {
    if (x_t == null)
      return initEpisode();
    VectorPool pool = VectorPools.pool(x_t);
    delta_t = r_tp1 + beta_tp1 * z_tp1 + (1 - beta_tp1) * v.dotProduct(x_bar_tp1) - v.dotProduct(x_t);
    e.update((1 - beta_tp1) * lambda_t * rho_t, x_t);
    MutableVector delta_e = pool.newVector(e.vect()).mapMultiplyToSelf(delta_t);
    MutableVector tdCorrection = pool.newVector();
    if (!Vectors.isNull(x_bar_tp1))
      tdCorrection.set(x_bar_tp1).mapMultiplyToSelf((1 - beta_tp1) * (1 - lambda_t) * e.vect().dotProduct(w));
    v.addToSelf(alpha_v, pool.newVector(delta_e).subtractToSelf(tdCorrection));
    w.addToSelf(alpha_w, delta_e.subtractToSelf(pool.newVector(x_t).mapMultiplyToSelf(w.dotProduct(x_t))));
    delta_e = null;
    pool.releaseAll();
    return delta_t;
  }

  @Override
  public double predict(RealVector x) {
    return v.dotProduct(x);
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
    e.vect().setEntry(index, 0);
    w.data[index] = 0;
  }

  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public Traces traces() {
    return e;
  }
}
