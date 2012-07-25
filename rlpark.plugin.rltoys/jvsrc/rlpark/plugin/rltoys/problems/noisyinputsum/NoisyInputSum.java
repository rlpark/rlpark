package rlpark.plugin.rltoys.problems.noisyinputsum;

import java.util.Random;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class NoisyInputSum {
  private final Random random;
  private int nbSteps = 0;
  @Monitor(level = 4)
  private final PVector weights;
  @Monitor(level = 4)
  private final PVector inputs;
  @Monitor
  private double target;
  private final int nbChangingWeights;
  private int changePeriod = 20;

  public NoisyInputSum(Random random, int nbNonZeroWeights, int nbInputs) {
    this(random, nbNonZeroWeights, nbNonZeroWeights, nbInputs);
  }

  public NoisyInputSum(Random random, int nbChangingWeights, int nbNonZeroWeights, int nbInputs) {
    this.random = random;
    this.nbChangingWeights = nbChangingWeights;
    weights = createWeights(random, nbNonZeroWeights, nbInputs);
    inputs = new PVector(nbInputs);
  }

  private PVector createWeights(Random random, int nbNonZeroWeights, int nbInputs) {
    PVector weights = new PVector(nbInputs);
    for (int i = 0; i < weights.size; i++)
      if (i < nbNonZeroWeights)
        weights.data[i] = random.nextBoolean() ? 1 : -1;
      else
        weights.data[i] = 0;
    return weights;
  }

  private void changeWeight() {
    weights.data[random.nextInt(nbChangingWeights)] *= -1;
  }

  public void step() {
    nbSteps++;
    if (nbSteps % changePeriod == 0)
      changeWeight();
    for (int i = 0; i < inputs.size; i++)
      inputs.data[i] = random.nextGaussian();
    target = weights.dotProduct(inputs);
  }

  public RealVector inputs() {
    return inputs;
  }

  public double target() {
    return target;
  }

  public void setChangePeriod(int changePeriod) {
    this.changePeriod = changePeriod;
  }
}
