package rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers;

import java.util.Arrays;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;

public class RandomPolicy implements PolicyDistribution {
  private static final long serialVersionUID = 7993101579423392389L;
  private final Random random;
  private final Action[] actions;

  public RandomPolicy(Random random, Action[] actions) {
    this.random = random;
    this.actions = actions.clone();
  }

  @Override
  public double pi(Action a) {
    assert Arrays.asList(a).contains(a);
    return 1.0 / actions.length;
  }

  @Override
  public Action sampleAction() {
    return Utils.choose(random, actions);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return new PVector[] {};
  }

  @Override
  public RealVector[] computeGradLog(Action a_t) {
    return new PVector[] {};
  }

  @Override
  public int nbParameterVectors() {
    return 0;
  }

  @Override
  public void update(RealVector x) {
  }
}
