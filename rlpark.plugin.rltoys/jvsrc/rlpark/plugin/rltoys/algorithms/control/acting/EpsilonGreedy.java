package rlpark.plugin.rltoys.algorithms.control.acting;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.utils.Utils;

public class EpsilonGreedy extends Greedy {
  private static final long serialVersionUID = -2618584767896890494L;
  private final double epsilon;
  private final Random random;

  public EpsilonGreedy(Random random, Action[] actions, StateToStateAction toStateAction, Predictor predictor,
      double epsilon) {
    super(predictor, actions, toStateAction);
    this.epsilon = epsilon;
    this.random = random;
  }

  @Override
  public Action sampleAction() {
    if (random.nextFloat() < epsilon)
      return Utils.choose(random, actions);
    return super.bestAction();
  }

  @Override
  public double pi(Action a) {
    double probability = a == bestAction ? 1.0 - epsilon : 0.0;
    return probability + epsilon / actions.length;
  }

  @Override
  public EpsilonGreedy duplicate() {
    return new EpsilonGreedy(random, actions, Utils.clone(toStateAction), predictor, epsilon);
  }
}
