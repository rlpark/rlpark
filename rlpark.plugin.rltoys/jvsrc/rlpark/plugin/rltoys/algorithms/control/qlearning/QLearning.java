package rlpark.plugin.rltoys.algorithms.control.qlearning;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.control.acting.Greedy;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.traces.EligibilityTraceAlgorithm;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class QLearning implements Predictor, LinearLearner, EligibilityTraceAlgorithm {
  private static final long serialVersionUID = -404558746167490755L;
  @Monitor(level = 4)
  protected final PVector theta;
  private final Traces e;
  private final double lambda;
  private final double gamma;
  private final double alpha;
  private final StateToStateAction toStateAction;
  private double delta;
  private final Greedy greedy;

  public QLearning(Action[] actions, double alpha, double gamma, double lambda, StateToStateAction toStateAction,
      Traces prototype) {
    this.alpha = alpha;
    this.gamma = gamma;
    this.lambda = lambda;
    this.toStateAction = toStateAction;
    greedy = new Greedy(this, actions, toStateAction);
    theta = new PVector(toStateAction.vectorSize());
    e = prototype.newTraces(toStateAction.vectorSize());
  }

  public double update(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    if (x_t == null)
      return initEpisode();
    greedy.update(x_t);
    Action at_star = greedy.bestAction();
    greedy.update(x_tp1);
    RealVector phi_sa_t = toStateAction.stateAction(x_t, a_t);
    delta = r_tp1 + gamma * greedy.bestActionValue() - theta.dotProduct(phi_sa_t);
    if (a_t == at_star)
      e.update(gamma * lambda, phi_sa_t);
    else {
      e.clear();
      e.update(0, phi_sa_t);
    }
    theta.addToSelf(alpha * delta, e.vect());
    return delta;
  }

  private double initEpisode() {
    if (e != null)
      e.clear();
    delta = 0.0;
    return delta;
  }

  @Override
  public double predict(RealVector phi_sa) {
    return theta.dotProduct(phi_sa);
  }

  public PVector theta() {
    return theta;
  }

  @Override
  public void resetWeight(int index) {
    theta.setEntry(index, 0);
  }

  @Override
  public PVector weights() {
    return theta;
  }

  @Override
  public double error() {
    return delta;
  }

  public Policy greedy() {
    return greedy;
  }

  @Override
  public Traces traces() {
    return e;
  }
}
