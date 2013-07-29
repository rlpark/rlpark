package rlpark.plugin.rltoys.algorithms.control.gq;

import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.math.vector.pool.VectorPool;
import rlpark.plugin.rltoys.math.vector.pool.VectorPools;
import rlpark.plugin.rltoys.utils.Prototype;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class GreedyGQ implements OffPolicyLearner {
  private static final long serialVersionUID = 7017521530598253457L;
  @Monitor
  protected final GQ gq;
  @Monitor
  protected final Policy target;
  protected final Policy behaviour;
  protected final StateToStateAction toStateAction;
  @Monitor
  public double rho_t;
  private final Action[] actions;
  private double delta_t;
  private final RealVector prototype;

  @SuppressWarnings("unchecked")
  public GreedyGQ(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target, Policy behaviour) {
    this.gq = gq;
    this.target = target;
    this.behaviour = behaviour;
    this.toStateAction = toStateAction;
    this.actions = actions;
    prototype = ((Prototype<RealVector>) gq.e).prototype();
  }

  public double update(RealVector x_t, Action a_t, double r_tp1, double gamma_tp1, double z_tp1, RealVector x_tp1,
      Action a_tp1) {
    rho_t = 0.0;
    if (a_t != null && !Vectors.isNull(x_t)) {
      target.update(x_t);
      behaviour.update(x_t);
      rho_t = target.pi(a_t) / behaviour.pi(a_t);
    }
    assert Utils.checkValue(rho_t);
    VectorPool pool = VectorPools.pool(prototype, gq.v.size);
    MutableVector sa_bar_tp1 = pool.newVector();
    if (!Vectors.isNull(x_t) && !Vectors.isNull(x_tp1)) {
      target.update(x_tp1);
      for (Action a : actions) {
        double pi = target.pi(a);
        if (pi == 0)
          continue;
        sa_bar_tp1.addToSelf(pi, toStateAction.stateAction(x_tp1, a));
      }
    }
    RealVector phi_stat = !Vectors.isNull(x_t) ? toStateAction.stateAction(x_t, a_t) : null;
    delta_t = gq.update(phi_stat, rho_t, r_tp1, sa_bar_tp1, z_tp1);
    pool.releaseAll();
    return delta_t;
  }

  public PVector theta() {
    return gq.v;
  }

  public double gamma() {
    return 1 - gq.beta_tp1;
  }

  public GQ gq() {
    return gq;
  }

  @Override
  public Policy targetPolicy() {
    return target;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward) {
    update(x_t, a_t, reward, gamma(), 0, x_tp1, a_tp1);
  }

  @Override
  public Action proposeAction(RealVector x_t) {
    return Policies.decide(target, x_t);
  }

  @Override
  public GQ predictor() {
    return gq;
  }
}
