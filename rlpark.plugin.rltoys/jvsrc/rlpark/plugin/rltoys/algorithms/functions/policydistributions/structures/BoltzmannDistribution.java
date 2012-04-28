package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.StochasticPolicy;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class BoltzmannDistribution extends StochasticPolicy implements PolicyDistribution {
  private static final long serialVersionUID = 7036360201611314726L;
  private final RealVector[] actionToPhi_sa;
  @Monitor(level = 4)
  private PVector u;
  private RealVector lastFeatureVector;
  private MutableVector averagePhi;
  private final StateToStateAction toStateAction;
  private final double[] distribution;

  public BoltzmannDistribution(Random random, Action[] actions, StateToStateAction toStateAction) {
    super(random, actions);
    this.toStateAction = toStateAction;
    distribution = new double[actions.length];
    actionToPhi_sa = new RealVector[actions.length];
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateDistributionIFN(s);
    return distribution[atoi(a)];
  }

  private void updateDistributionIFN(RealVector s) {
    if (lastFeatureVector == s)
      return;
    lastFeatureVector = s;
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
      actionToPhi_sa[a_i] = phi_sa;
    }
    for (int i = 0; i < distribution.length; i++) {
      distribution[i] /= sum;
      assert Utils.checkValue(distribution[i]);
    }
    averagePhi.mapMultiplyToSelf(1.0 / sum);
  }

  protected Action initialize() {
    lastFeatureVector = null;
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
    return new RealVector[] { actionToPhi_sa[actionToIndex.get(a_t)].subtract(averagePhi) };
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
