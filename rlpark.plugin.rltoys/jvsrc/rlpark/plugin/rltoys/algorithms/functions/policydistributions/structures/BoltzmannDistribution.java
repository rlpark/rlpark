package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.StochasticPolicy;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class BoltzmannDistribution extends StochasticPolicy implements PolicyDistribution {
  private static final long serialVersionUID = 7036360201611314726L;
  private final MutableVector[] xa;
  @Monitor(level = 4)
  private PVector u;
  private MutableVector xaBar;
  private MutableVector gradBuffer;
  private final StateToStateAction toStateAction;
  private final double[] distribution;
  @Monitor
  private final Range linearRangeOverall = new Range(1.0, 1.0);
  @Monitor
  private final Range linearRangeAveraged = new Range(1.0, 1.0);

  public BoltzmannDistribution(Random random, Action[] actions, StateToStateAction toStateAction) {
    super(random, actions);
    assert toStateAction != null;
    this.toStateAction = toStateAction;
    distribution = new double[actions.length];
    xa = new MutableVector[actions.length];
  }

  @Override
  public double pi(Action a) {
    return distribution[atoi(a)];
  }

  @Override
  public void update(RealVector x) {
    linearRangeAveraged.reset();
    double sum = 0;
    clearBuffers(x);
    for (int a_i = 0; a_i < actions.length; a_i++) {
      xa[a_i].set(toStateAction.stateAction(x, actions[a_i]));
      final double linearCombination = u.dotProduct(xa[a_i]);
      linearRangeOverall.update(linearCombination);
      linearRangeAveraged.update(linearCombination);
      double probabilityNotNormalized = Math.exp(linearCombination);
      assert Utils.checkValue(probabilityNotNormalized);
      distribution[a_i] = probabilityNotNormalized;
      sum += probabilityNotNormalized;
      xaBar.addToSelf(probabilityNotNormalized, xa[a_i]);
    }
    for (int i = 0; i < distribution.length; i++) {
      distribution[i] /= sum;
      assert Utils.checkValue(distribution[i]);
    }
    xaBar.mapMultiplyToSelf(1.0 / sum);
  }

  private void clearBuffers(RealVector x) {
    if (xaBar == null) {
      xaBar = toStateAction.stateAction(x, actions[0]).newInstance(u.size);
      gradBuffer = xaBar.newInstance(u.size);
      for (int i = 0; i < xa.length; i++)
        xa[i] = xaBar.newInstance(u.size);
      return;
    }
    xaBar.clear();
  }

  @Override
  public Action sampleAction() {
    return chooseAction(distribution);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    u = new PVector(toStateAction.vectorSize());
    return new PVector[] { u };
  }

  @Override
  public RealVector[] computeGradLog(Action a_t) {
    gradBuffer.clear();
    gradBuffer.set(xa[atoi(a_t)]);
    return new RealVector[] { gradBuffer.subtractToSelf(xaBar) };
  }

  @Override
  public int nbParameterVectors() {
    return 1;
  }

  @Override
  public double[] distribution() {
    return distribution;
  }

  static public double probaToLinearValue(int nbAction, double proba) {
    double max = Math.log(proba * (nbAction - 1)) - Math.log(1 - proba);
    assert proba > .5 && max > 0 || proba < .5 && max < 0;
    return max;
  }
}
