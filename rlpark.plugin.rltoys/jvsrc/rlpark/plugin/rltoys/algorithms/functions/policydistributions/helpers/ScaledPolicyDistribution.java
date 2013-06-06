package rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.BoundedPdf;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyParameterized;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.policy.BoundedPolicy;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ScaledPolicyDistribution implements BoundedPdf, PolicyParameterized {
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
  public double pi(Action a) {
    return policy.pi(problemToPolicy(ActionArray.toDouble(a)));
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return policy.createParameters(nbFeatures);
  }

  @Override
  public Action sampleAction() {
    return policyToProblem(ActionArray.toDouble(policy.sampleAction()));
  }

  @Override
  public RealVector[] computeGradLog(Action a_t) {
    return policy.computeGradLog(problemToPolicy(ActionArray.toDouble(a_t)));
  }

  private ActionArray policyToProblem(double policyAction) {
    double normalizedAction = normalize(policyRange, policyAction);
    return new ActionArray(scale(problemRange, normalizedAction));
  }

  protected ActionArray problemToPolicy(double problemAction) {
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
  public double piMax() {
    return ((BoundedPdf) policy).piMax();
  }


  @Override
  public void update(RealVector x) {
    policy.update(x);
  }

  @Override
  public void setParameters(PVector... parameters) {
    ((PolicyParameterized) policy).setParameters(parameters);
  }


  @Override
  public PVector[] parameters() {
    return ((PolicyParameterized) policy).parameters();
  }
}
