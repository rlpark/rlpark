package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import static rlpark.plugin.rltoys.utils.Utils.square;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class NormalDistribution extends AbstractNormalDistribution {
  private static final long serialVersionUID = -4074721193363280217L;
  protected double sigma2;
  private final double initialMean;
  private final double initialStddev;

  public NormalDistribution(Random random, double mean, double sigma) {
    super(random);
    initialMean = mean;
    initialStddev = sigma;
  }

  @Override
  public RealVector[] computeGradLog(Action a) {
    updateSteps(ActionArray.toDouble(a));
    gradMean.set(x).mapMultiplyToSelf(meanStep);
    gradStddev.set(x).mapMultiplyToSelf(stddevStep);
    assert Vectors.checkValues(gradMean) && Vectors.checkValues(gradStddev);
    return new RealVector[] { gradMean, gradStddev };
  }

  protected void updateSteps(double a) {
    meanStep = (a - mean) / sigma2;
    stddevStep = square(a - mean) / sigma2 - 1;
  }

  @Override
  public Action sampleAction() {
    a_t = random.nextGaussian() * stddev + mean;
    if (!Utils.checkValue(a_t))
      return null;
    return new ActionArray(a_t);
  }

  @Override
  protected void updateDistribution() {
    mean = u_mean.dotProduct(x) + initialMean;
    stddev = Math.exp(u_stddev.dotProduct(x)) * initialStddev + Utils.EPSILON;
    sigma2 = square(stddev);
    assert Utils.checkValue(mean) && Utils.checkValue(sigma2);
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
  public double piMax() {
    return Math.max(pi(new ActionArray(mean)), Utils.EPSILON);
  }
}
