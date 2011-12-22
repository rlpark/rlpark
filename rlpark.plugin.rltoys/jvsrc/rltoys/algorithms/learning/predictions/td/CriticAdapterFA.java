package rltoys.algorithms.learning.predictions.td;

import rltoys.environments.envio.states.Projector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;


public class CriticAdapterFA implements OffPolicyTD {
  private static final long serialVersionUID = 4767252828929104353L;
  private final OffPolicyTD offPolicyTD;
  private final Projector projector;
  private PVector o_t;
  private PVector o_tp1;
  private RealVector x_t;
  private RealVector x_tp1;

  public CriticAdapterFA(Projector projector, OffPolicyTD offPolicyTD) {
    this.projector = projector;
    this.offPolicyTD = offPolicyTD;
  }

  @Override
  public void resetWeight(int index) {
    offPolicyTD.resetWeight(index);
  }

  @Override
  public PVector weights() {
    return offPolicyTD.weights();
  }

  private RealVector projectIFN(RealVector o) {
    if (o == o_t)
      return x_t;
    if (o == o_tp1)
      return x_tp1;
    o_t = o_tp1;
    x_t = x_tp1;
    o_tp1 = (PVector) o;
    x_tp1 = projector.project(o_tp1.data);
    return x_tp1;
  }

  @Override
  public double predict(RealVector x) {
    return offPolicyTD.predict(projectIFN(x));
  }

  @Override
  public double error() {
    return offPolicyTD.error();
  }

  @Override
  public double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1) {
    return offPolicyTD.update(rho_t, projectIFN(x_t), projectIFN(x_tp1), r_tp1);
  }

  @Override
  public double prediction() {
    return offPolicyTD.prediction();
  }

  @Override
  public PVector secondaryWeights() {
    return offPolicyTD.secondaryWeights();
  }
}