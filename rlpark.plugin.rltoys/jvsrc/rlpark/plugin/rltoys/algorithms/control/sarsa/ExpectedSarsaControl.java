package rlpark.plugin.rltoys.algorithms.control.sarsa;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;

public class ExpectedSarsaControl extends SarsaControl {
  private static final long serialVersionUID = 738626133717186128L;
  private final Action[] actions;

  public ExpectedSarsaControl(Action[] actions, Policy acting, StateToStateAction toStateAction, Sarsa sarsa) {
    super(acting, toStateAction, sarsa);
    this.actions = actions;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    acting.update(x_tp1);
    Action a_tp1 = acting.sampleAction();
    RealVector xa_tp1 = null;
    VectorPool pool = VectorPools.pool(x_tp1, sarsa.q.size);
    MutableVector phi_bar_tp1 = pool.newVector();
    if (x_tp1 != null) {
      for (Action a : actions) {
        double pi = acting.pi(a);
        if (pi == 0.0) {
          assert a != a_tp1;
          continue;
        }
        RealVector phi_stp1a = toStateAction.stateAction(x_tp1, a);
        if (a == a_tp1)
          xa_tp1 = phi_stp1a.copy();
        phi_bar_tp1.addToSelf(pi, phi_stp1a);
      }
    }
    sarsa.update(x_t != null ? xa_t : null, xa_tp1, r_tp1);
    xa_t = xa_tp1;
    pool.releaseAll();
    return a_tp1;
  }

  @Override
  public Policy acting() {
    return acting;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(acting, x);
  }
}
