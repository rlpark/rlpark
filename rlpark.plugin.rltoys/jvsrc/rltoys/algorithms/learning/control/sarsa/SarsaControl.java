package rltoys.algorithms.learning.control.sarsa;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SarsaControl implements Control {
  private static final long serialVersionUID = 2848271828496458933L;
  @Monitor
  protected final Sarsa sarsa;
  @Monitor
  protected final Policy acting;
  protected final StateToStateAction toStateAction;
  protected RealVector xa_t = null;

  public SarsaControl(Policy acting, StateToStateAction toStateAction, Sarsa sarsa) {
    this.sarsa = sarsa;
    this.toStateAction = toStateAction;
    this.acting = acting;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    if (x_t == null)
      xa_t = null;
    Action a_tp1 = acting.decide(x_tp1);
    RealVector xa_tp1 = toStateAction.stateAction(x_tp1, a_tp1);
    sarsa.update(xa_t, xa_tp1, r_tp1);
    xa_t = xa_tp1;
    return a_tp1;
  }

  public Policy acting() {
    return acting;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return acting.decide(x);
  }
}
