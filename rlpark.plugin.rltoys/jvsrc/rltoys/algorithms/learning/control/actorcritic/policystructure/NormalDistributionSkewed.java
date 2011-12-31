package rltoys.algorithms.learning.control.actorcritic.policystructure;

import static rltoys.utils.Utils.square;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.actions.Actions;
import rltoys.math.vector.RealVector;

public class NormalDistributionSkewed extends NormalDistribution {
  private static final long serialVersionUID = -8287545926699668326L;

  public NormalDistributionSkewed(Random random, double mean, double sigma) {
    this(random, mean, sigma, 1.0);
  }

  public NormalDistributionSkewed(Random random, double mean, double sigma, double stddevGradientFactor) {
    super(random, mean, sigma, stddevGradientFactor);
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    assert Actions.isOneDimension(a_t);
    updateDistributionIFN(x_t);
    double a = ActionArray.toDouble(a_t);
    RealVector meanGradient = x_t.mapMultiply(a - mean);
    RealVector stddevGradient = x_t.mapMultiply(stddevGradientFactor * (square(a - mean) / square(stddev) - 1));
    lastX = null;
    return new RealVector[] { meanGradient, stddevGradient };
  }
}
