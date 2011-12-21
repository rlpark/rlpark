package rltoys.algorithms.learning.control.sarsa;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.SVector;

public class ExpectedSarsaControl extends SarsaControl {
  private static final long serialVersionUID = 738626133717186128L;
  private final Action[] actions;

  public ExpectedSarsaControl(Action[] actions, Policy acting, StateToStateAction toStateAction, Sarsa sarsa) {
    super(acting, toStateAction, sarsa);
    this.actions = actions;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    if (x_t == null)
      xa_t = null;
    Action a_tp1 = acting.decide(x_tp1);
    RealVector xa_tp1 = null;
    SVector phi_bar_tp1 = null;
    if (x_tp1 != null) {
      phi_bar_tp1 = new SVector(sarsa.theta.size);
      for (Action a : actions) {
        double pi = acting.pi(x_tp1, a);
        if (pi == 0.0) {
          assert a != a_tp1;
          continue;
        }
        RealVector phi_stp1a = toStateAction.stateAction(x_tp1, a);
        if (a == a_tp1)
          xa_tp1 = phi_stp1a;
        phi_bar_tp1.addToSelf(phi_stp1a.mapMultiply(pi));
      }
    }
    sarsa.update(xa_t, xa_tp1, r_tp1);
    xa_t = xa_tp1;
    return a_tp1;
  }

  @Override
  public Policy acting() {
    return acting;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return acting.decide(x);
  }
}
