package rlpark.plugin.rltoys.algorithms.control.sarsa;

import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;

public class SarsaAlphaBound extends Sarsa {
  private static final long serialVersionUID = -7883369665747113840L;

  public SarsaAlphaBound(double alpha, double gamma, double lambda, int nbFeatures) {
    this(alpha, gamma, lambda, nbFeatures, new ATraces());
  }

  public SarsaAlphaBound(double alpha, double gamma, double lambda, int nbFeatures, Traces prototype) {
    this(alpha, gamma, lambda, new PVector(nbFeatures), prototype);
  }

  public SarsaAlphaBound(double alpha, double gamma, double lambda, PVector q, Traces prototype) {
    super(alpha, gamma, lambda, q, prototype);
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1) {
    if (x_t == null)
      return initEpisode();
    v_tp1 = x_tp1 != null ? q.dotProduct(x_tp1) : 0;
    v_t = q.dotProduct(x_t);
    delta = r_tp1 + gamma * v_tp1 - v_t;
    e.update(gamma * lambda, x_t);
    updateAlpha(x_t, x_tp1);
    q.addToSelf(alpha * delta, e.vect());
    return delta;
  }

  private void updateAlpha(RealVector x_t, RealVector x_tp1) {
    VectorPool pool = VectorPools.pool(x_t);
    RealVector gammaXtp1 = pool.newVector(x_tp1).mapMultiplyToSelf(gamma);
    RealVector xMinusGammaXtp1 = pool.newVector(x_t).subtractToSelf(gammaXtp1);
    double b = Math.abs(e.vect().dotProduct(xMinusGammaXtp1));
    if (b > 0)
      alpha = Math.min(alpha, 1.0 / b);
    pool.releaseAll();
  }
}
