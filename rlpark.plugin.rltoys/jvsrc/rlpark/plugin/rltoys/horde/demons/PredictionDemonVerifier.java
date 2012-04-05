package rlpark.plugin.rltoys.horde.demons;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambdaAutostep;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.utils.NotImplemented;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@SuppressWarnings("restriction")
public class PredictionDemonVerifier implements Serializable {
  private static final long serialVersionUID = 6127406364376542150L;
  private final PredictionDemon predictionDemon;
  private final RewardFunction rewardFunction;
  private final int offsetLength;
  private final double[] gammas;
  private final double[] predictionHistory;
  private final double[] observedHistory;
  private int current;
  private boolean cacheFilled;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double error;
  @Monitor
  private double prediction, observed;
  private boolean errorComputed;
  private final double precision;
  private final double gamma;

  public PredictionDemonVerifier(PredictionDemon predictionDemon) {
    this(predictionDemon, 0.01);
  }

  public PredictionDemonVerifier(PredictionDemon predictionDemon, double precision) {
    this.predictionDemon = predictionDemon;
    rewardFunction = predictionDemon.rewardFunction();
    gamma = extractGamma(predictionDemon.predicter());
    this.precision = precision;
    offsetLength = gamma > 0 ? (int) Math.ceil(Math.log(precision) / Math.log(gamma)) : 1;
    predictionHistory = new double[offsetLength];
    observedHistory = new double[offsetLength];
    gammas = new double[offsetLength];
    for (int i = 0; i < gammas.length; i++)
      gammas[i] = Math.pow(gamma, i);
    current = 0;
    cacheFilled = false;
  }

  public double extractGamma(OnPolicyTD learner) {
    if (learner instanceof TD)
      return ((TD) learner).gamma();
    if (learner instanceof TDLambdaAutostep)
      return ((TDLambdaAutostep) learner).gamma();
    throw new NotImplemented();
  }

  private void reset() {
    current = 0;
    cacheFilled = false;
    errorComputed = false;
    error = 0;
    prediction = 0;
    observed = 0;
  }

  public double update(boolean endOfEpisode) {
    if (endOfEpisode) {
      reset();
      return 0.0;
    }
    if (cacheFilled) {
      errorComputed = true;
      prediction = predictionHistory[current];
      observed = observedHistory[current];
      error = prediction - observed;
    }
    double reward = rewardFunction.reward();
    observedHistory[current] = 0;
    for (int i = 0; i < offsetLength; i++)
      observedHistory[(current - i + offsetLength) % offsetLength] += reward * gammas[i];
    predictionHistory[current] = predictionDemon.prediction();
    updateCurrent();
    return error;
  }

  protected void updateCurrent() {
    current++;
    if (current >= offsetLength) {
      cacheFilled = true;
      current = 0;
    }
  }

  public double error() {
    return error;
  }

  public boolean errorComputed() {
    return errorComputed;
  }

  public double precision() {
    return precision;
  }

  public double returnValue() {
    return observed;
  }

  public double gamma() {
    return gamma;
  }
}
