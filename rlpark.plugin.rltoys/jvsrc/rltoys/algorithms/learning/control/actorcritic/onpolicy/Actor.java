package rltoys.algorithms.learning.control.actorcritic.onpolicy;

import java.io.Serializable;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.utils.Utils;
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
    this.policyDistribution = policyDistribution;
    this.alpha_u = alpha_u;
    u = policyDistribution.createParameters(nbFeatures);
  }

  public void update(RealVector x_t, Action a_t, double delta) {
    if (x_t == null)
      return;
    RealVector[] gradLog = policyDistribution.getGradLog(x_t, a_t);
    for (int i = 0; i < u.length; i++)
      u[i].addToSelf(alpha_u[i] * delta, gradLog[i]);
  }

  public Action proposeAction(RealVector x) {
    return policyDistribution.decide(x);
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

  public PVector[] parameters() {
    return u;
  }

  @LabelProvider(ids = { "u" })
  String labelOf(int index) {
    if (policyDistribution instanceof LabeledCollection)
      return ((LabeledCollection) policyDistribution).label(index);
    return null;
  }
}
