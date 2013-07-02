package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class IDBD implements LearningAlgorithm {
  private static final long serialVersionUID = 6961877310325699208L;
  public final static double MinimumStepsize = 1e-6;
  private final double theta;
  @Monitor(level = 4)
  private final PVector weights;
  @Monitor(level = 4)
  private final PVector alphas;
  @Monitor(level = 4)
  private final PVector hs;

  public IDBD(int size, double theta) {
    this(size, theta, .1 / size);
  }

  public IDBD(int size, double theta, double alphaInit) {
    this.theta = theta;
    weights = new PVector(size);
    alphas = new PVector(size);
    alphas.set(alphaInit);
    hs = new PVector(size);
  }

  @Override
  public double learn(RealVector x_t, double y_tp1) {
    VectorPool pool = VectorPools.pool(x_t);
    double delta = y_tp1 - predict(x_t);
    MutableVector deltaX = pool.newVector(x_t).mapMultiplyToSelf(delta);
    RealVector deltaXH = pool.newVector(deltaX).ebeMultiplyToSelf(hs);
    PVectors.multiplySelfByExponential(alphas, theta, deltaXH, MinimumStepsize);
    RealVector alphaDeltaX = deltaX.ebeMultiplyToSelf(alphas);
    deltaX = null;
    weights.addToSelf(alphaDeltaX);
    RealVector alphaX2 = pool.newVector(x_t).ebeMultiplyToSelf(x_t).ebeMultiplyToSelf(alphas).ebeMultiplyToSelf(hs);
    hs.addToSelf(-1, alphaX2);
    hs.addToSelf(alphaDeltaX);
    pool.releaseAll();
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  public PVector alphas() {
    return alphas;
  }

  public RealVector h() {
    return hs;
  }
}
