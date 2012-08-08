package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ActorLambdaOffPolicy extends AbstractActorOffPolicy {
  final protected Traces[] e_u;
  final public double lambda;
  final protected double alpha_u;
  @Monitor(level = 4)
  final protected PVector[] u;

  public ActorLambdaOffPolicy(double lambda, double gamma, PolicyDistribution policyDistribution, double alpha_u,
      int nbFeatures, Traces prototype) {
    super(policyDistribution);
    u = policyDistribution.createParameters(nbFeatures);
    this.alpha_u = alpha_u;
    this.lambda = lambda;
    e_u = new Traces[u.length];
    for (int i = 0; i < e_u.length; i++)
      e_u[i] = prototype.newTraces(nbFeatures);
  }

  protected void updateEligibilityTraces(double rho_t, Action a_t, double delta) {
    RealVector[] gradLog = policyDistribution.computeGradLog(a_t);
    for (int i = 0; i < u.length; i++) {
      e_u[i].update(lambda, gradLog[i]);
      e_u[i].vect().mapMultiplyToSelf(rho_t);
    }
  }

  protected void updatePolicyParameters(double rho_t, Action a_t, double delta) {
    for (int i = 0; i < u.length; i++)
      u[i].addToSelf(alpha_u * delta, e_u[i].vect());
  }

  @Override
  protected void updateParameters(double rho_t, RealVector x_t, Action a_t, double delta) {
    policyDistribution.update(x_t);
    updateEligibilityTraces(rho_t, a_t, delta);
    updatePolicyParameters(rho_t, a_t, delta);
  }

  @Override
  protected void initEpisode() {
    for (Traces e : e_u)
      e.clear();
  }

  public Traces[] eligibilities() {
    return e_u;
  }

  @Override
  public PVector[] actorParameters() {
    return u;
  }
}
