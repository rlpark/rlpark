package rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.acting.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.representations.acting.StochasticPolicy;
import rlpark.plugin.rltoys.algorithms.representations.actions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class BoltzmannDistribution extends StochasticPolicy implements PolicyDistribution {
  private static final long serialVersionUID = 7036360201611314726L;
  private final Map<Action, RealVector> actionToPhi_sa = new LinkedHashMap<Action, RealVector>();
  @Monitor(level = 4)
  private PVector u;
  private RealVector lastS;
  private MutableVector averagePhi;
  private final StateToStateAction toStateAction;
  private final double[] distribution;

  public BoltzmannDistribution(Random random, Action[] actions, StateToStateAction toStateAction) {
    super(random, actions);
    this.toStateAction = toStateAction;
    distribution = new double[actions.length];
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateDistributionIFN(s);
    return distribution[atoi(a)];
  }

  private void updateDistributionIFN(RealVector s) {
    if (lastS == s)
      return;
    lastS = s;
    double sum = 0;
    averagePhi = null;
    for (int a_i = 0; a_i < actions.length; a_i++) {
      RealVector phi_sa = toStateAction.stateAction(s, actions[a_i]);
      double probabilityNotNormalized = Math.exp(u.dotProduct(phi_sa));
      distribution[a_i] = probabilityNotNormalized;
      sum += probabilityNotNormalized;
      if (averagePhi == null)
        averagePhi = phi_sa.newInstance(u.size);
      averagePhi.addToSelf(phi_sa.mapMultiply(probabilityNotNormalized));
      actionToPhi_sa.put(actions[a_i], phi_sa);
    }
    for (int i = 0; i < distribution.length; i++) {
      distribution[i] /= sum;
      assert Utils.checkValue(distribution[i]);
    }
    averagePhi.mapMultiplyToSelf(1.0 / sum);
  }

  protected Action initialize() {
    lastS = null;
    return null;
  }

  @Override
  public Action decide(RealVector x_t) {
    if (x_t == null)
      return initialize();
    updateDistributionIFN(x_t);
    return chooseAction(distribution);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    u = new PVector(toStateAction.vectorSize());
    return new PVector[] { u };
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    lastS = null;
    return new RealVector[] { actionToPhi_sa.get(a_t).subtract(averagePhi) };
  }

  @Override
  public int nbParameterVectors() {
    return 1;
  }

  @Override
  public double[] distribution() {
    return distribution;
  }
}
