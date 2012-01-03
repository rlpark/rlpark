package rltoys.algorithms.representations.acting;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.ranges.Range;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ScaledPolicyDistribution implements PolicyDistribution {
  private static final long serialVersionUID = -7521424991872961399L;
  @Monitor
  private final PolicyDistribution policy;
  private final double[] means;
  private final double[] scales;

  public ScaledPolicyDistribution(PolicyDistribution policy, Range actionRange) {
    this(policy, new Range[] { actionRange });
  }

  public ScaledPolicyDistribution(PolicyDistribution policy, Range[] actionRanges) {
    this.policy = policy;
    means = new double[actionRanges.length];
    scales = new double[actionRanges.length];
    for (int i = 0; i < actionRanges.length; i++) {
      means[i] = (actionRanges[i].max() + actionRanges[i].min()) / 2.0;
      scales[i] = (actionRanges[i].max() - actionRanges[i].min()) / 2.0;
    }
  }

  @Override
  public double pi(RealVector s, Action a) {
    return policy.pi(s, problemToAgent((ActionArray) a));
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return policy.createParameters(nbFeatures);
  }

  @Override
  public Action decide(RealVector s) {
    return agentToProblem((ActionArray) policy.decide(s));
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    return policy.getGradLog(x_t, problemToAgent((ActionArray) a_t));
  }

  private ActionArray agentToProblem(ActionArray agentAction) {
    double[] scaled = new double[agentAction.actions.length];
    for (int i = 0; i < scaled.length; i++)
      scaled[i] = (agentAction.actions[i] * scales[i]) + means[i];
    return new ActionArray(scaled);
  }

  private ActionArray problemToAgent(ActionArray problemAction) {
    double[] scaled = new double[problemAction.actions.length];
    for (int i = 0; i < scaled.length; i++)
      scaled[i] = (problemAction.actions[i] - means[i]) / scales[i];
    return new ActionArray(scaled);
  }
}
