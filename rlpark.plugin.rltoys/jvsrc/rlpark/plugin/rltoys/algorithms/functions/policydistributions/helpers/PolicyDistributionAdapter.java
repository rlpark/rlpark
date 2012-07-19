package rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class PolicyDistributionAdapter implements PolicyDistribution {
  private static final long serialVersionUID = -3702175603455756729L;
  private final Policy policy;

  public PolicyDistributionAdapter(Policy policy) {
    this.policy = policy;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return policy.pi(s, a);
  }

  @Override
  public Action decide(RealVector s) {
    return policy.decide(s);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return new PVector[] {};
  }

  @Override
  public RealVector[] computeGradLog(RealVector x_t, Action a_t) {
    return new PVector[] {};
  }

  @Override
  public int nbParameterVectors() {
    return 0;
  }
}
