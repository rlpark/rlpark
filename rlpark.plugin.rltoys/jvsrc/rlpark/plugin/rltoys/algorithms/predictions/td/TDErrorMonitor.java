package rlpark.plugin.rltoys.algorithms.predictions.td;

import java.io.Serializable;

import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@SuppressWarnings("restriction")
public class TDErrorMonitor implements Serializable {
  private static final long serialVersionUID = 6441800170099052600L;
  private final int bufferSize;
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

  public TDErrorMonitor(double gamma, double precision) {
    this.gamma = gamma;
    this.precision = precision;
    bufferSize = computeBufferSize(gamma, precision);
    predictionHistory = new double[bufferSize];
    observedHistory = new double[bufferSize];
    gammas = new double[bufferSize];
    for (int i = 0; i < gammas.length; i++)
      gammas[i] = Math.pow(gamma, i);
    current = 0;
    cacheFilled = false;
  }

  static public int computeBufferSize(double gamma, double precision) {
    return gamma > 0 ? (int) Math.ceil(Math.log(precision) / Math.log(gamma)) : 1;
  }

  private void reset() {
    current = 0;
    cacheFilled = false;
    errorComputed = false;
    error = 0;
    prediction = 0;
    observed = 0;
  }

  public double update(double prediction_t, double reward_tp1, boolean endOfEpisode) {
    if (endOfEpisode) {
      reset();
      return 0.0;
    }
    if (cacheFilled) {
      errorComputed = true;
      prediction = predictionHistory[current];
      observed = observedHistory[current];
      error = observed - prediction;
    }
    observedHistory[current] = 0;
    for (int i = 0; i < bufferSize; i++)
      observedHistory[(current - i + bufferSize) % bufferSize] += reward_tp1 * gammas[i];
    predictionHistory[current] = prediction_t;
    updateCurrent();
    return error;
  }

  protected void updateCurrent() {
    current++;
    if (current >= bufferSize) {
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

  public int bufferSize() {
    return bufferSize;
  }

  public double prediction() {
    return prediction;
  }
}
