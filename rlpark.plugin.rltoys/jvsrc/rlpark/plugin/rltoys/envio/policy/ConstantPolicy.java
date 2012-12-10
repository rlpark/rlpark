package rlpark.plugin.rltoys.envio.policy;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class ConstantPolicy extends StochasticPolicy {
  private static final long serialVersionUID = 9106677500699183729L;
  protected final double[] distribution;

  public ConstantPolicy(Random random, Action[] actions, double[] distribution) {
    super(random, actions);
    assert actions.length == distribution.length;
    this.distribution = distribution;
  }

  @Override
  public double pi(Action a) {
    return distribution[atoi(a)];
  }

  @Override
  public Action sampleAction() {
    return chooseAction(distribution);
  }

  @Override
  public double[] distribution() {
    return distribution;
  }

  @Override
  public void update(RealVector x) {
  }
}
