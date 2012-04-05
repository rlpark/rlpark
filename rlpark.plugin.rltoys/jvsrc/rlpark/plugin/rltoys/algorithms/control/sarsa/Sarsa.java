package rlpark.plugin.rltoys.algorithms.control.sarsa;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class Sarsa implements Predictor {
  private static final long serialVersionUID = 9030254074554565900L;
  @Monitor(level = 4)
  protected final Traces e;
  @Monitor(level = 4)
  protected final PVector theta;
  protected final double lambda;
  protected final double gamma;
  protected final double alpha;
  private double delta;
  private double v_t;
  private double v_tp1;

  public Sarsa(double alpha, double gamma, double lambda, int nbFeatures) {
    this(alpha, gamma, lambda, nbFeatures, new ATraces());
  }

  public Sarsa(double alpha, double gamma, double lambda, int nbFeatures, Traces prototype) {
    this.alpha = alpha;
    this.gamma = gamma;
    this.lambda = lambda;
    theta = new PVector(nbFeatures);
    e = prototype.newTraces(nbFeatures);
  }

  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    v_tp1 = phi_tp1 != null ? theta.dotProduct(phi_tp1) : 0;
    v_t = theta.dotProduct(phi_t);
    delta = r_tp1 + gamma * v_tp1 - v_t;
    e.update(gamma * lambda, phi_t);
    theta.addToSelf(alpha * delta, e.vect());
    return delta;
  }

  protected double initEpisode() {
    e.clear();
    return 0.0;
  }

  @Override
  public double predict(RealVector phi_sa) {
    return theta.dotProduct(phi_sa);
  }

  public PVector theta() {
    return theta;
  }
}
