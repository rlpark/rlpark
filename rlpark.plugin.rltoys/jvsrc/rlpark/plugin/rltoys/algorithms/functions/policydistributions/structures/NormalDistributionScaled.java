package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import static rlpark.plugin.rltoys.utils.Utils.square;

import java.util.Random;

public class NormalDistributionScaled extends NormalDistribution {
  private static final long serialVersionUID = -8287545926699668326L;

  public NormalDistributionScaled(Random random, double mean, double sigma) {
    super(random, mean, sigma);
  }

  @Override
  protected void updateSteps(double a) {
    meanStep = a - mean;
    stddevStep = square(a - mean) - sigma2;
  }
}
