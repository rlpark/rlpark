package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.VectorNull;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class CriticAdapterFA implements OffPolicyTD {
  private static final long serialVersionUID = 4767252828929104353L;
  @Monitor
  private final OffPolicyTD offPolicyTD;
  private final Projector projector;
  private RealVector x_t = null;

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
    if (o == null)
      return new VectorNull(projector.vectorSize());
    return projector.project(((PVector) o).data);
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
  public double update(double pi_t, double b_t, RealVector o_t, RealVector o_tp1, double r_tp1) {
    RealVector x_tp1 = projectIFN(o_tp1);
    double delta = offPolicyTD.update(pi_t, b_t, x_t, x_tp1, r_tp1);
    x_t = Vectors.isNull(x_tp1) ? null : Vectors.bufferedCopy(x_tp1, x_t);
    return delta;
  }

  @Override
  public double prediction() {
    return offPolicyTD.prediction();
  }

  @Override
  public PVector secondaryWeights() {
    return offPolicyTD.secondaryWeights();
  }

  public Projector projector() {
    return projector;
  }

  public Predictor predictor() {
    return offPolicyTD;
  }
}