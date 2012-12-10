package rlpark.plugin.rltoys.algorithms.control.sarsa;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SarsaControl implements ControlLearner {
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
    Action a_tp1 = Policies.decide(acting, x_tp1);
    RealVector xa_tp1 = toStateAction.stateAction(x_tp1, a_tp1);
    sarsa.update(x_t != null ? xa_t : null, xa_tp1, r_tp1);
    xa_t = Vectors.bufferedCopy(xa_tp1, xa_t);
    return a_tp1;
  }

  public Policy acting() {
    return acting;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(acting, x);
  }
}
