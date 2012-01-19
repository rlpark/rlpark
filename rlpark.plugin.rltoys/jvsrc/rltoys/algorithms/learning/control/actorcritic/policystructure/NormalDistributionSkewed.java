package rltoys.algorithms.learning.control.actorcritic.policystructure;

import static rltoys.utils.Utils.square;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.vector.RealVector;

public class NormalDistributionSkewed extends NormalDistribution {
  private static final long serialVersionUID = -8287545926699668326L;

  public NormalDistributionSkewed(Random random, double mean, double sigma) {
    super(random, mean, sigma);
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    double sigma2 = square(stddev);
    double a = ActionArray.toDouble(a_t);
    meanStep = a - mean;
    stddevStep = square(a - mean) / sigma2 - 1;
    return new RealVector[] { x_t.mapMultiply(meanStep), x_t.mapMultiply(stddevStep) };
  }
}
