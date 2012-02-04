package rltoys.algorithms.representations.acting;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public class ConstantPolicy extends StochasticPolicy {
  private static final long serialVersionUID = 9106677500699183729L;
  protected final double[] distribution;

  public ConstantPolicy(Random random, Action[] actions, double[] distribution) {
    super(random, actions);
    assert actions.length == distribution.length;
    this.distribution = distribution;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return distribution[atoi(a)];
  }

  @Override
  public Action decide(RealVector s) {
    return chooseAction(distribution);
  }

  @Override
  public double[] distribution() {
    return distribution;
  }
}
