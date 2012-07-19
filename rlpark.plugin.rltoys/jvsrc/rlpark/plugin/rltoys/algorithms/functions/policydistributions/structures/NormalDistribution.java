package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import static rlpark.plugin.rltoys.utils.Utils.square;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class NormalDistribution extends AbstractNormalDistribution {
  private static final long serialVersionUID = -4074721193363280217L;
  protected double sigma2;

  public NormalDistribution(Random random, double mean, double sigma) {
    super(random, mean, sigma);
  }

  @Override
  public RealVector[] computeGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    updateSteps(ActionArray.toDouble(a_t));
    return new RealVector[] { x_t.mapMultiply(meanStep), x_t.mapMultiply(stddevStep) };
  }

  protected void updateSteps(double a) {
    meanStep = (a - mean) / sigma2;
    stddevStep = square(a - mean) / sigma2 - 1;
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
    sigma2 = square(stddev);
  }

  @Override
  public double pi_s(double a) {
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
