package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.Random;

import rltoys.algorithms.representations.acting.BoundedPdf;
import rltoys.algorithms.representations.acting.BoundedPolicy;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.actions.Actions;
import rltoys.math.ranges.Range;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

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
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    assert Actions.isOneDimension(a_t);
    return new PVector[] {};
  }

  @Override
  public Action decide(RealVector x) {
    return new ActionArray(range.choose(random));
  }

  @Override
  public double pi(RealVector s, Action action) {
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
  public double piMax(RealVector s) {
    return pdfValue;
  }
}
