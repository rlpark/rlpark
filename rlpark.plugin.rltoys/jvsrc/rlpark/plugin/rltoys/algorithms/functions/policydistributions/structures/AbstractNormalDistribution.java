package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.BoundedPdf;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.actions.Actions;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public abstract class AbstractNormalDistribution implements PolicyDistribution, LabeledCollection, BoundedPdf {
  private static final long serialVersionUID = -6707070542157254303L;
  public final double initialMean;
  public final double initialStddev;
  @Monitor(level = 4)
  protected PVector u_mean;
  @Monitor(level = 4)
  protected PVector u_stddev;
  protected double mean;
  protected double stddev;
  protected final Random random;
  public double a_t;
  protected double meanStep;
  protected double stddevStep;
  protected RealVector x;

  public AbstractNormalDistribution(Random random, double mean, double sigma) {
    initialMean = mean;
    initialStddev = sigma;
    this.mean = initialMean;
    this.stddev = sigma;
    this.random = random;
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    u_mean = new PVector(nbFeatures);
    u_stddev = new PVector(nbFeatures);
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
    this.x = x.copy();
    updateDistribution();
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
