package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class ActorLambdaOffPolicy extends AbstractActorOffPolicy {
  final protected Traces[] e_u;
  final public double lambda;
  final protected double alpha_u;
  private double rho_t;

  public ActorLambdaOffPolicy(double lambda, double gamma, PolicyDistribution policyDistribution, double alpha_u,
      int nbFeatures, Traces prototype) {
    this(policyDistribution.createParameters(nbFeatures), lambda, gamma, policyDistribution, alpha_u, prototype);
  }

  public ActorLambdaOffPolicy(PVector[] policyParameters, double lambda, double gamma,
      PolicyDistribution policyDistribution, double alpha_u, Traces prototype) {
    super(policyParameters, policyDistribution);
    this.alpha_u = alpha_u;
    this.lambda = lambda;
    e_u = new Traces[u.length];
    for (int i = 0; i < e_u.length; i++)
      e_u[i] = prototype.newTraces(u[i].size);
  }

  protected void updateEligibilityTraces(double rho_t, Action a_t, double delta) {
    RealVector[] gradLog = targetPolicy.computeGradLog(a_t);
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
  protected void updateParameters(double pi_t, double b_t, RealVector x_t, Action a_t, double delta) {
    targetPolicy.update(x_t);
    rho_t = pi_t / b_t;
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
}
