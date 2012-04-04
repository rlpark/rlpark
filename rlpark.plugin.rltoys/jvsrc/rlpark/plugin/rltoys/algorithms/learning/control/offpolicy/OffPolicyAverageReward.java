package rlpark.plugin.rltoys.algorithms.learning.control.offpolicy;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class OffPolicyAverageReward implements Serializable {
  private static final long serialVersionUID = 2902286201980261522L;
  private final double alpha_j;
  @Monitor
  private double r;
  @Monitor
  private double j;
  @Monitor
  private double r_diff;
  private final Policy targetPolicy;
  private final Policy behaviorPolicy;

  public OffPolicyAverageReward(double alpha_j, Policy behaviorPolicy, Policy targetPolicy) {
    this.alpha_j = alpha_j;
    this.behaviorPolicy = behaviorPolicy;
    this.targetPolicy = targetPolicy;
  }

  public double average(RealVector x_t, Action a_t, double r_tp1) {
    double rho_t = x_t != null ? targetPolicy.pi(x_t, a_t) / behaviorPolicy.pi(x_t, a_t) : 0.0;
    this.r = r_tp1;
    j = (1 - alpha_j) * j + alpha_j * rho_t * r;
    r_diff = r - j;
    return r_diff;
  }
}
