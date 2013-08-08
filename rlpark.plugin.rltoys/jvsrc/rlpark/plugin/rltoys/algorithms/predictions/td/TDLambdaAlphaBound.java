package rlpark.plugin.rltoys.algorithms.predictions.td;


import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;

/**
 * TD Lambda with Adaptive Step-Size
 * 
 * See Adaptive Step-Size for Online Temporal Difference Learning William Dabney
 * and Andrew G. Barto (2012)
 * 
 */
public class TDLambdaAlphaBound extends TDLambda {
  private static final long serialVersionUID = -1404196112258948883L;

  public TDLambdaAlphaBound(double lambda, double gamma, double alpha, int nbFeatures) {
    this(lambda, gamma, alpha, nbFeatures, new ATraces());
  }

  public TDLambdaAlphaBound(double lambda, double gamma, double alpha, int nbFeatures, Traces prototype) {
    super(lambda, gamma, alpha, nbFeatures, prototype);
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    e.update(lambda * gamma_t, x_t);
    updateAlpha(x_t, x_tp1, gamma_tp1);
    v.addToSelf(alpha_v * delta_t, e.vect());
    gamma_t = gamma_tp1;
    return delta_t;
  }

  private void updateAlpha(RealVector x_t, RealVector x_tp1, double gamma_tp1) {
    VectorPool pool = VectorPools.pool(x_t);
    RealVector gammaXtp1 = pool.newVector(x_tp1).mapMultiplyToSelf(gamma_tp1);
    RealVector xMinusGammaXtp1 = pool.newVector(x_t).subtractToSelf(gammaXtp1);
    double b = Math.abs(e.vect().dotProduct(xMinusGammaXtp1));
    if (b > 0)
      alpha_v = Math.min(alpha_v, 1.0 / b);
    pool.releaseAll();
  }
}
