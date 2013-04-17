package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Actor implements Serializable {
  private static final long serialVersionUID = 3063342634037779182L;
  public final double alpha_u[];
  @Monitor(level = 4)
  protected final PVector[] u;
  @Monitor
  protected final PolicyDistribution policyDistribution;

  public Actor(PolicyDistribution policyDistribution, double alpha_u, int nbFeatures) {
    this(policyDistribution, Utils.newFilledArray(policyDistribution.nbParameterVectors(), alpha_u), nbFeatures);
  }

  public Actor(PolicyDistribution policyDistribution, double[] alpha_u, int nbFeatures) {
    this(policyDistribution.createParameters(nbFeatures), policyDistribution, alpha_u);
  }

  public Actor(PVector[] policyParameters, PolicyDistribution policyDistribution, double[] alpha_u) {
    this.policyDistribution = policyDistribution;
    this.alpha_u = alpha_u;
    u = policyParameters;
  }

  public void update(RealVector x_t, Action a_t, double delta) {
    if (x_t == null)
      return;
    RealVector[] gradLog = policyDistribution.computeGradLog(a_t);
    for (int i = 0; i < u.length; i++)
      u[i].addToSelf(alpha_u[i] * delta, gradLog[i]);
  }

  public PolicyDistribution policy() {
    return policyDistribution;
  }

  public int vectorSize() {
    int result = 0;
    for (PVector v : u)
      result += v.size;
    return result;
  }

  public PVector[] actorParameters() {
    return u;
  }

  @LabelProvider(ids = { "u" })
  protected String labelOf(int index) {
    if (policyDistribution instanceof LabeledCollection)
      return ((LabeledCollection) policyDistribution).label(index);
    return null;
  }
}
