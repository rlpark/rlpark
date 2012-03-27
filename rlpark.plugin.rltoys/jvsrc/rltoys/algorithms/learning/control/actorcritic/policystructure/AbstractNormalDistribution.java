package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.Random;

import rltoys.algorithms.representations.acting.BoundedPdf;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.actions.Actions;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
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
  @IgnoreMonitor
  protected RealVector lastX;
  public double a_t;
  protected double meanStep;
  protected double stddevStep;

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

  protected ActionArray initialize() {
    lastX = null;
    return null;
  }

  public void updateDistributionIFN(RealVector x) {
    if (lastX == x)
      return;
    lastX = x;
    if (lastX == null)
      return;
    updateDistribution(x);
  }

  abstract protected void updateDistribution(RealVector x);

  public double stddev() {
    return stddev;
  }

  public double mean() {
    return mean;
  }

  @Override
  public double pi(RealVector s, Action a) {
    assert Actions.isOneDimension(a);
    updateDistributionIFN(s);
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
