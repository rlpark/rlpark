package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.filters.Filters;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVectors;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class Autostep implements LearningAlgorithm, LinearLearner {
  private static final long serialVersionUID = -3311074550497156281L;
  private static final double DefaultMetaStepSize = 0.01;
  private final double Tau = 10000;
  @Monitor(level = 4)
  protected final PVector alphas;
  @Monitor(level = 4)
  protected final PVector weights;
  @Monitor(level = 4)
  protected final PVector h;
  private final double kappa;
  @Monitor(level = 4)
  private final PVector v;
  private double delta;
  private double prediction;

  public Autostep(PVector weights) {
    this(weights, DefaultMetaStepSize, 1.0);
  }


  public Autostep(int vectorSize) {
    this(new PVector(vectorSize));
  }

  public Autostep(int vectorSize, double kappa, double initStepsize) {
    this(new PVector(vectorSize), kappa, initStepsize);
  }

  public Autostep(PVector weights, double kappa, double initStepsize) {
    this.weights = weights;
    this.kappa = kappa;
    int nbFeatures = weights.size;
    alphas = new PVector(nbFeatures);
    alphas.set(initStepsize);
    h = new PVector(nbFeatures);
    v = new PVector(nbFeatures);
    v.set(1.0);
  }

  protected void updateAlphas(VectorPool pool, RealVector x, RealVector x2, RealVector deltaX) {
    MutableVector deltaXH = pool.newVector(deltaX).ebeMultiplyToSelf(h);
    MutableVector absDeltaXH = Vectors.absToSelf(pool.newVector(deltaXH));
    MutableVector sparseV = pool.newVector();
    Vectors.toBinary(sparseV, deltaX).ebeMultiplyToSelf(v);
    MutableVector vUpdate = pool.newVector(absDeltaXH).subtractToSelf(sparseV).ebeMultiplyToSelf(x2)
        .ebeMultiplyToSelf(alphas);
    v.addToSelf(1.0 / Tau, vUpdate);
    Vectors.positiveMaxToSelf(v, absDeltaXH);
    PVectors.multiplySelfByExponential(alphas, kappa, deltaXH.ebeDivideToSelf(v), IDBD.MinimumStepsize);
    deltaXH = null;
    RealVector x2ByAlphas = pool.newVector(x2).ebeMultiplyToSelf(alphas);
    double sum = x2ByAlphas.sum();
    if (sum > 1)
      Filters.mapMultiplyToSelf(alphas, 1 / sum, x);
  }

  @Override
  public double learn(RealVector x_t, double y_tp1) {
    VectorPool pool = VectorPools.pool(x_t);
    prediction = predict(x_t);
    delta = y_tp1 - prediction;
    MutableVector deltaX = pool.newVector(x_t).mapMultiplyToSelf(delta);
    MutableVector x2 = pool.newVector(x_t).ebeMultiplyToSelf(x_t);
    updateAlphas(pool, x_t, x2, deltaX);
    RealVector alphasDeltaX = deltaX.ebeMultiplyToSelf(alphas);
    deltaX = null;
    weights.addToSelf(alphasDeltaX);
    MutableVector x2AlphasH = x2.ebeMultiplyToSelf(alphas).ebeMultiplyToSelf(h);
    x2 = null;
    h.addToSelf(-1, x2AlphasH).addToSelf(alphasDeltaX);
    pool.releaseAll();
    return delta;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  @Override
  public PVector weights() {
    return weights;
  }

  public PVector alphas() {
    return alphas;
  }


  @Override
  public void resetWeight(int index) {
    weights.setEntry(index, 0);
  }


  @Override
  public double error() {
    return delta;
  }
}
