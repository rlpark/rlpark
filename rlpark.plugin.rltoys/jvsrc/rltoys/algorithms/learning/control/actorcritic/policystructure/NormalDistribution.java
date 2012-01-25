package rltoys.algorithms.learning.control.actorcritic.policystructure;

import static rltoys.utils.Utils.square;

import java.util.Random;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class NormalDistribution extends AbstractNormalDistribution {
  private static final long serialVersionUID = -4074721193363280217L;

  public NormalDistribution(Random random, double mean, double sigma) {
    super(random, mean, sigma);
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    double sigma2 = square(stddev);
    double a = ActionArray.toDouble(a_t);
    meanStep = (a - mean) / sigma2;
    stddevStep = square(a - mean) / sigma2 - 1;
    return new RealVector[] { x_t.mapMultiply(meanStep), x_t.mapMultiply(stddevStep) };
  }

  @Override
  public Action decide(RealVector x_t) {
    if (x_t == null)
      return initialize();
    updateDistributionIFN(x_t);
    a_t = random.nextGaussian() * stddev + mean;
    if (!Utils.checkValue(a_t))
      return null;
    return new ActionArray(a_t);
  }

  @Override
  protected void updateDistribution(RealVector x) {
    mean = u_mean.dotProduct(x) + initialMean;
    stddev = Math.exp(u_stddev.dotProduct(x)) * initialStddev + Utils.EPSILON;
  }

  @Override
  public double pi_s(double a) {
    double sigma2 = stddev * stddev;
    double ammu2 = (a - mean) * (a - mean);
    return Math.exp(-ammu2 / (2 * sigma2)) / Math.sqrt(2 * Math.PI * sigma2);
  }

  static public JointDistribution newJointDistribution(Random random, int nbNormalDistribution, double mean,
      double sigma) {
    PolicyDistribution[] distributions = new PolicyDistribution[nbNormalDistribution];
    for (int i = 0; i < distributions.length; i++)
      distributions[i] = new NormalDistribution(random, mean, sigma);
    return new JointDistribution(distributions);
  }

  @Override
  public double piMax(RealVector s) {
    return Math.max(pi(s, new ActionArray(mean)), Utils.EPSILON);
  }
}
