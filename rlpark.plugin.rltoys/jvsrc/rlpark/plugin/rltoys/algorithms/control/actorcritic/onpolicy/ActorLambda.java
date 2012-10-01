package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class ActorLambda extends Actor {
  private static final long serialVersionUID = -1601184295976574511L;
  public final Traces[] e_u;
  private final double lambda;
  private final double gamma;

  public ActorLambda(double lambda, double gamma, PolicyDistribution policyDistribution, double alpha_u, int nbFeatures) {
    this(lambda, gamma, policyDistribution, alpha_u, nbFeatures, new ATraces());
  }

  public ActorLambda(double lambda, double gamma, PolicyDistribution policyDistribution, double alpha_u,
      int nbFeatures, Traces prototype) {
    this(lambda, gamma, policyDistribution, Utils.newFilledArray(policyDistribution.nbParameterVectors(), alpha_u),
         nbFeatures, prototype);
  }

  public ActorLambda(double lambda, double gamma, PolicyDistribution policyDistribution, double[] alpha_u,
      int nbFeatures, Traces prototype) {
    super(policyDistribution, alpha_u, nbFeatures);
    this.lambda = lambda;
    this.gamma = gamma;
    e_u = new Traces[policyDistribution.nbParameterVectors()];
    for (int i = 0; i < e_u.length; i++)
      e_u[i] = prototype.newTraces(u[i].size);
  }

  @Override
  public void update(RealVector x_t, Action a_t, double delta) {
    if (x_t == null) {
      initEpisode();
      return;
    }
    RealVector[] gradLog = policyDistribution.computeGradLog(a_t);
    for (int i = 0; i < u.length; i++)
      e_u[i].update(gamma * lambda, gradLog[i]);
    updatePolicyParameters(gradLog, delta);
  }

  protected void updatePolicyParameters(RealVector[] gradLog, double delta) {
    for (int i = 0; i < u.length; i++)
      u[i].addToSelf(alpha_u[i] * delta, e_u[i].vect());
  }

  private void initEpisode() {
    for (Traces e : e_u)
      e.clear();
  }

  @LabelProvider(ids = { "e_u" })
  String eligiblityLabelOf(int index) {
    return super.labelOf(index);
  }
}
