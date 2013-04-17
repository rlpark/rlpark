package rlpark.plugin.rltoys.algorithms.control.gq;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;

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
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    VectorPool pool = VectorPools.pool(x_tp1, toStateAction.vectorSize());
    RealVector xa_t = x_t != null ? pool.newVector(toStateAction.stateAction(x_t, a_t)) : null;
    Action a_tp1 = Policies.decide(acting, x_tp1);
    gq.update(xa_t, 1.0, r_tp1, toStateAction.stateAction(x_tp1, a_tp1), 0.0);
    pool.releaseAll();
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
