package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;

public class IDBD implements LearningAlgorithm {
  private static final long serialVersionUID = 6961877310325699208L;
  public final static double MinimumStepsize = 10e-7;
  private final double theta;
  private final PVector weights;
  private final PVector alphas;
  private final PVector hs;

  public IDBD(int size, double theta) {
    this.theta = theta;
    weights = new PVector(size);
    alphas = new PVector(size);
    alphas.set(0.1 / size);
    hs = new PVector(size);
  }

  @Override
  public double learn(RealVector x, double y) {
    VectorPool pool = VectorPools.pool(x);
    double delta = y - predict(x);
    MutableVector deltaX = pool.newVector(x).mapMultiplyToSelf(delta);
    RealVector deltaXH = pool.newVector(deltaX).ebeMultiplyToSelf(hs);
    PVectors.multiplySelfByExponential(alphas, theta, deltaXH, MinimumStepsize);
    RealVector alphaDeltaX = deltaX.ebeMultiplyToSelf(alphas);
    deltaX = null;
    weights.addToSelf(alphaDeltaX);
    RealVector alphaX2 = pool.newVector(x).ebeMultiplyToSelf(x).ebeMultiplyToSelf(alphas).ebeMultiplyToSelf(hs);
    hs.addToSelf(-1, alphaX2);
    hs.addToSelf(alphaDeltaX);
    pool.releaseAll();
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }
}
