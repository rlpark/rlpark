package rltoys.algorithms.representations.acting;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.ranges.Range;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ScaledPolicyDistribution implements PolicyDistribution, BoundedPdf {
  private static final long serialVersionUID = -7521424991872961399L;
  @Monitor
  protected final PolicyDistribution policy;
  protected final Range policyRange;
  protected final Range problemRange;

  public ScaledPolicyDistribution(BoundedPolicy policy, Range problemRange) {
    this((PolicyDistribution) policy, policy.range(), problemRange);
  }


  public ScaledPolicyDistribution(PolicyDistribution policy, Range policyRange, Range problemRange) {
    this.policy = policy;
    this.policyRange = policyRange;
    this.problemRange = problemRange;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return policy.pi(s, problemToPolicy(ActionArray.toDouble(a)));
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return policy.createParameters(nbFeatures);
  }

  @Override
  public Action decide(RealVector s) {
    return policyToProblem(ActionArray.toDouble(policy.decide(s)));
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    return policy.getGradLog(x_t, problemToPolicy(ActionArray.toDouble(a_t)));
  }

  private ActionArray policyToProblem(double policyAction) {
    double normalizedAction = normalize(policyRange, policyAction);
    return new ActionArray(scale(problemRange, normalizedAction));
  }

  private ActionArray problemToPolicy(double problemAction) {
    double normalizedAction = normalize(problemRange, problemAction);
    return new ActionArray(scale(policyRange, normalizedAction));
  }

  private double normalize(Range range, double a) {
    return (a - range.center()) / (range.length() / 2.0);
  }

  private double scale(Range range, double a) {
    return (a * (range.length() / 2.0)) + range.center();
  }

  @Override
  public int nbParameterVectors() {
    return policy.nbParameterVectors();
  }


  @Override
  public double piMax(RealVector s) {
    return ((BoundedPdf) policy).piMax(s);
  }
}
