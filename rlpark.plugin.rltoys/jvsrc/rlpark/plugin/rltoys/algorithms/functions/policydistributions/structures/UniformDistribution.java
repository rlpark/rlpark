package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.BoundedPdf;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.actions.Actions;
import rlpark.plugin.rltoys.envio.policy.BoundedPolicy;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class UniformDistribution implements PolicyDistribution, BoundedPolicy, BoundedPdf {
  private static final long serialVersionUID = 7284864369595009279L;
  private final Random random;
  private final Range range;
  private final double pdfValue;

  public UniformDistribution(Random random, Range range) {
    this.random = random;
    this.range = range;
    pdfValue = 1.0 / range.length();
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return new PVector[] {};
  }

  @Override
  public RealVector[] computeGradLog(Action a_t) {
    assert Actions.isOneDimension(a_t);
    return new PVector[] {};
  }

  @Override
  public Action sampleAction() {
    return new ActionArray(range.choose(random));
  }

  @Override
  public double pi(Action action) {
    assert ((ActionArray) action).actions.length == 1;
    double a = ActionArray.toDouble(action);
    return range.in(a) ? pdfValue : 0;
  }

  @Override
  public int nbParameterVectors() {
    return 0;
  }

  @Override
  public Range range() {
    return range;
  }

  @Override
  public double piMax() {
    return pdfValue;
  }

  @Override
  public void update(RealVector x) {
  }
}
