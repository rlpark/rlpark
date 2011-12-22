package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.math.vector.RealVector;

public class GQOnPolicyControl implements ControlLearner {
  private static final long serialVersionUID = -1583554276099167880L;
  private final GQ gq;
  private final StateToStateAction toStateAction;
  private final Policy acting;

  public GQOnPolicyControl(Policy acting, StateToStateAction toStateAction, GQ gq) {
    this.gq = gq;
    this.toStateAction = toStateAction;
    this.acting = acting;
  }

  @Override
  public Action step(RealVector s_t, Action a_t, RealVector s_tp1, double r_tp1) {
    RealVector phi_t = toStateAction.stateAction(s_t, a_t);
    Action a_tp1 = acting.decide(s_tp1);
    gq.update(phi_t, 1.0, r_tp1, toStateAction.stateAction(s_tp1, a_tp1), 0.0);
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
