package rlpark.plugin.rltoys.algorithms.predictions.td;

import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
@SuppressWarnings("restriction")
public class TDLambdaAutostep implements OnPolicyTD {
  private static final long serialVersionUID = 1567652945995637498L;
  protected double mu = 0.01;
  protected double tau = 1000;
  @Monitor(level = 4)
  final private PVector v;
  private double v_t;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double delta_t;

  final protected Traces e;
  @Monitor(level = 4)
  final protected PVector alpha;
  @Monitor(level = 4)
  final protected PVector h;
  @Monitor(level = 4)
  final protected PVector normalizer;
  protected double maxOneM2;
  private final double gamma;
  private final double lambda;
  private double m;
  double tempM = 0.0;
  private final double lowerNumericalBound;

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures) {
    this(lambda, gamma, 0.1, nbFeatures, new ATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures, Traces prototype) {
    this(lambda, gamma, 0.1, nbFeatures, prototype);
  }

  public TDLambdaAutostep(double lambda, double gamma, double initAlpha, int nbFeatures) {
    this(lambda, gamma, initAlpha, nbFeatures, new ATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, double initAlpha, int nbFeatures, Traces prototype) {
    this.lambda = lambda;
    e = prototype.newTraces(nbFeatures);
    this.gamma = gamma;
    v = new PVector(nbFeatures);
    alpha = new PVector(nbFeatures);
    alpha.set(initAlpha);
    h = new PVector(nbFeatures);
    normalizer = new PVector(nbFeatures);
    normalizer.set(1.0);
    lowerNumericalBound = Math.pow(10.0, -10) / nbFeatures;
  }

  public void setMu(double mu) {
    this.mu = mu;
  }

  public void setTau(double tau) {
    this.tau = tau;
  }

  protected double initEpisode() {
    e.clear();
    return 0;
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + gamma * v.dotProduct(x_tp1) - v_t;
    e.update(lambda * gamma, x_t);
    PVector densePhi = new PVector(x_t.accessData());
    if (e.vect() instanceof SVector)
      updateNormalizationAndStepSizeSparse(delta_t, densePhi.data);
    else if (e.vect() instanceof DenseVector)
      updateNormalizationAndStepSizeDense(delta_t, densePhi.data);
    else
      throw new RuntimeException("Not implemented");
    MutableVector eAlpha = e.vect().ebeMultiply(alpha);
    MutableVector alphaDeltaE = eAlpha.mapMultiply(delta_t);
    v.addToSelf(alphaDeltaE);
    h.addToSelf(alphaDeltaE.subtractToSelf(Vectors.absToSelf(eAlpha.ebeMultiplyToSelf(densePhi)).ebeMultiplyToSelf(h)));
    return delta_t;
  }

  private void updateNormalizationAndStepSizeDense(double delta_t, double[] densePhi) {
    final double[] normalizerData = normalizer.data;
    final double[] alphaData = alpha.data;
    final double[] data = ((DenseVector) e.vect()).accessData();
    for (int i = 0; i < data.length; i++)
      updateStepSizeNormalizers(densePhi, normalizerData, alphaData, i, data[i], delta_t);
    m = 0.0;
    for (int i = 0; i < data.length; i++)
      m += featureNorm(densePhi, alphaData, i, data[i]);
    maxOneM2 = Math.max(1, m);
    for (int index = 0; index < data.length; index++)
      if (densePhi[index] != 0)
        alphaData[index] /= maxOneM2;
  }

  private void updateNormalizationAndStepSizeSparse(double delta_t, double[] densePhi) {
    final double[] normalizerData = normalizer.data;
    final double[] alphaData = alpha.data;
    final SVector se = (SVector) e.vect();
    for (int i = 0; i < se.nonZeroElements(); i++)
      updateStepSizeNormalizers(densePhi, normalizerData, alphaData, se.activeIndexes[i], se.values[i], delta_t);
    m = 0.0;
    for (int i = 0; i < se.nonZeroElements(); i++)
      m += featureNorm(densePhi, alphaData, se.activeIndexes[i], se.values[i]);
    maxOneM2 = Math.max(1, m);
    for (int index : se.activeIndexes)
      if (densePhi[index] != 0)
        alphaData[index] /= maxOneM2;
  }

  private void updateStepSizeNormalizers(double[] densePhi, final double[] normalizerData, final double[] alphaData,
      int eIndex, double eValue, final double delta_t) {
    double absDeltaEH = computeAbsDeltaEH(eIndex, eValue, delta_t);
    normalizerData[eIndex] = Math.max(absDeltaEH,
                                      normalizerData[eIndex] + (featureNorm(densePhi, alphaData, eIndex, eValue) / tau)
                                          * (absDeltaEH - normalizerData[eIndex]));
    normalizerData[eIndex] = Math.max(lowerNumericalBound, normalizerData[eIndex]);
    alphaData[eIndex] = alphaData[eIndex] * Math.exp(mu * delta_t * eValue * h.data[eIndex] / normalizerData[eIndex]);
    alphaData[eIndex] = Math.max(lowerNumericalBound, alphaData[eIndex]);
  }

  private double featureNorm(double[] densePhi, final double[] alphaData, int index, double value) {
    return alphaData[index] * Math.abs(value * densePhi[index]);
  }

  private double computeAbsDeltaEH(int index, double traceValue, double delta) {
    return Math.abs(traceValue * h.data[index] * delta);
  }

  public Traces eligibility() {
    return e;
  }

  @Override
  public double predict(RealVector phi) {
    return v.dotProduct(phi);
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
    alpha.data[index] = .1;
    h.data[index] = 0;
    normalizer.data[index] = 0;
    e.vect().setEntry(index, 0);
  }

  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public double prediction() {
    return v_t;
  }

  public double gamma() {
    return gamma;
  }
}
