package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.filters.Filters;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Autostep implements LearningAlgorithm {
  private static final long serialVersionUID = -3311074550497156281L;
  private final double Tau = 1000;
  @Monitor(level = 4)
  protected final PVector alphas;
  @Monitor(level = 4)
  protected final PVector weights;
  @Monitor(level = 4)
  protected final PVector h;
  private final PVector v;
  private double delta;

  public Autostep(int vectorSize) {
    this(.1 / vectorSize, new PVector(vectorSize));
  }

  public Autostep(double initStepsize, PVector weights) {
    this.weights = weights;
    int nbFeatures = weights.size;
    alphas = new PVector(nbFeatures);
    alphas.set(initStepsize);
    h = new PVector(nbFeatures);
    v = new PVector(nbFeatures);
    v.set(1.0);
  }

  protected void updateAlphas(VectorPool pool, RealVector x, RealVector x2, RealVector deltaX) {
    MutableVector deltaXH = pool.newVector(deltaX).ebeMultiplyToSelf(h);
    MutableVector absDeltaXH = pool.newVector(deltaXH);
    Vectors.absToSelf(absDeltaXH);
    MutableVector sparseV = pool.newVector();
    Vectors.toBinary(sparseV, deltaX).ebeMultiplyToSelf(v);
    MutableVector vUpdate = pool.newVector(absDeltaXH).subtractToSelf(sparseV).ebeMultiplyToSelf(x2)
        .ebeMultiplyToSelf(alphas);
    v.addToSelf(1.0 / Tau, vUpdate);
    Vectors.positiveMaxToSelf(v, absDeltaXH);
    PVectors.multiplySelfByExponential(alphas, .01, deltaXH.ebeDivideToSelf(v), IDBD.MinimumStepsize);
    deltaXH = null;
    RealVector x2ByAlphas = pool.newVector(x2).ebeMultiplyToSelf(alphas);
    double sum = Math.max(x2ByAlphas.sum(), 1);
    if (sum > 1)
      Filters.mapMultiplyToSelf(alphas, 1 / sum, x);
  }

  @Override
  public double learn(RealVector x, double y) {
    VectorPool pool = VectorPools.pool(x);
    delta = y - predict(x);
    MutableVector deltaX = pool.newVector(x).mapMultiplyToSelf(delta);
    MutableVector x2 = pool.newVector(x).ebeMultiplyToSelf(x);
    updateAlphas(pool, x, x2, deltaX);
    RealVector alphasDeltaX = deltaX.ebeMultiplyToSelf(alphas);
    deltaX = null;
    weights.addToSelf(alphasDeltaX);
    MutableVector minusX2AlphasH = x2.ebeMultiplyToSelf(alphas).ebeMultiplyToSelf(h);
    x2 = null;
    h.addToSelf(minusX2AlphasH.mapMultiplyToSelf(-1)).addToSelf(alphasDeltaX);
    pool.releaseAll();
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  public PVector weights() {
    return weights;
  }

  public PVector alphas() {
    return alphas;
  }
}
