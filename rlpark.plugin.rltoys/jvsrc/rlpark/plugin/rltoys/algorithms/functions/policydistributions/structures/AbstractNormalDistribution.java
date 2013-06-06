package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.BoundedPdf;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyParameterized;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.actions.Actions;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
@SuppressWarnings("restriction")
public abstract class AbstractNormalDistribution implements PolicyParameterized, LabeledCollection, BoundedPdf {
  private static final long serialVersionUID = -6707070542157254303L;
  @Monitor(level = 4)
  protected PVector u_mean;
  @Monitor(level = 4)
  protected PVector u_stddev;
  @Monitor(wrappers = { Abs.ID })
  protected double mean = 0;
  protected double stddev = 0;
  protected final Random random;
  public double a_t;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double meanStep;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double stddevStep;

  protected RealVector x = null;
  protected MutableVector gradMean = null;
  protected MutableVector gradStddev = null;

  public AbstractNormalDistribution(Random random) {
    this.random = random;
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    setParameters(new PVector(nbFeatures), new PVector(nbFeatures));
    return new PVector[] { u_mean, u_stddev };
  }

  @Override
  public void setParameters(PVector... u) {
    assert u.length == 2;
    u_mean = u[0];
    u_stddev = u[1];
  }

  @Override
  public PVector[] parameters() {
    return new PVector[] { u_mean, u_stddev };
  }

  public double stddev() {
    return stddev;
  }

  public double mean() {
    return mean;
  }

  @Override
  final public void update(RealVector x) {
    if (this.x == null)
      allocateBuffers(x);
    ((MutableVector) this.x).set(x);
    updateDistribution();
  }

  protected void allocateBuffers(RealVector prototype) {
    x = prototype.copyAsMutable();
    gradMean = prototype.copyAsMutable();
    gradStddev = prototype.copyAsMutable();
  }

  abstract protected void updateDistribution();

  @Override
  public double pi(Action a) {
    assert Actions.isOneDimension(a);
    return pi_s(ActionArray.toDouble(a));
  }

  public abstract double pi_s(double a);

  @Override
  public String label(int index) {
    return index == 0 ? "mean" : "stddev";
  }

  @Override
  public int nbParameterVectors() {
    return 2;
  }
}
