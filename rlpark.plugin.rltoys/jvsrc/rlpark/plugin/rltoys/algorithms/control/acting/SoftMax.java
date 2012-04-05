package rlpark.plugin.rltoys.algorithms.control.acting;

import java.util.Arrays;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.StochasticPolicy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;

public class SoftMax extends StochasticPolicy {
  private static final long serialVersionUID = -2129719316562814077L;
  private final StateToStateAction toStateAction;
  private final double temperature;
  private final Predictor predictor;
  private final double[] distribution;
  private RealVector last;

  public SoftMax(Random random, Predictor predictor, Action[] actions, StateToStateAction toStateAction,
      double temperature) {
    super(random, actions);
    this.toStateAction = toStateAction;
    this.temperature = temperature;
    this.predictor = predictor;
    distribution = new double[actions.length];
  }

  public SoftMax(Random random, Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this(random, predictor, actions, toStateAction, 1);
  }

  @Override
  public Action decide(RealVector s) {
    updateActionDistributionIFN(s);
    return chooseAction(distribution);
  }

  private void updateActionDistributionIFN(RealVector s) {
    if (last == s)
      return;
    last = s;
    if (last == null)
      return;
    double sum = 0.0;
    for (int i = 0; i < actions.length; i++) {
      Action action = actions[i];
      RealVector phi_sa = toStateAction.stateAction(s, action);
      double value = Math.exp(predictor.predict(phi_sa) / temperature);
      assert Utils.checkValue(value);
      sum += value;
      distribution[i] = value;
    }
    if (sum == 0) {
      Arrays.fill(distribution, 1.0);
      sum = distribution.length;
    }
    for (int i = 0; i < distribution.length; i++) {
      distribution[i] /= sum;
      assert Utils.checkValue(distribution[i]);
    }
    assert checkDistribution(distribution);
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateActionDistributionIFN(s);
    return distribution[atoi(a)];
  }

  @Override
  public double[] distribution() {
    return distribution;
  }
}
