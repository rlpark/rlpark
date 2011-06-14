package rltoys.algorithms.representations.ltu;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import rltoys.math.vector.BVector;
import rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class RandomNetworkAdaptive extends RandomNetwork {
  private static final long serialVersionUID = 1847556584654367004L;
  public final int minUnitActive;
  public final int maxUnitActive;
  public final double minDensity;
  public final double maxDensity;
  private final Random random;
  private int missingUnit;
  private int overUnit;

  public RandomNetworkAdaptive(Random random, int inputSize, int outputSize, double minDensity, double maxDensity) {
    super(inputSize, outputSize);
    this.random = random;
    this.minDensity = minDensity;
    this.maxDensity = maxDensity;
    this.minUnitActive = (int) (minDensity * outputSize);
    this.maxUnitActive = (int) (maxDensity * outputSize);
  }

  @Override
  public BVector project(BinaryVector obs) {
    BVector projected = super.project(obs);
    missingUnit = 0;
    overUnit = 0;
    int nbActive = projected.nonZeroElements();
    if (nbActive > maxUnitActive)
      decreaseDensity(obs);
    if (nbActive < minUnitActive)
      increaseDensity(obs);
    return projected;
  }

  private void increaseDensity(BinaryVector obs) {
    missingUnit = minUnitActive - output.nonZeroElements();
    Set<LTUAdaptiveDensity> couldHaveAgree = buildCouldHaveAgreeUnits(obs);
    assert missingUnit > 0;
    double selectionProbability = Math.min(1.0, missingUnit / (double) couldHaveAgree.size());
    for (LTUAdaptiveDensity ltu : couldHaveAgree) {
      if (random.nextFloat() > selectionProbability)
        continue;
      ltu.increaseDensity(random, obs);
    }
  }

  private Set<LTUAdaptiveDensity> buildCouldHaveAgreeUnits(BinaryVector obs) {
    Set<LTUAdaptiveDensity> couldHaveAgree = new LinkedHashSet<LTUAdaptiveDensity>();
    for (Integer activeInput : obs) {
      for (LTU ltu : parents(activeInput)) {
        if (ltu == null || ltu.isActive())
          continue;
        if (!(ltu instanceof LTUAdaptiveDensity))
          continue;
        couldHaveAgree.add((LTUAdaptiveDensity) ltu);
      }
    }
    return couldHaveAgree;
  }

  private void decreaseDensity(BinaryVector obs) {
    overUnit = output.nonZeroElements() - maxUnitActive;
    assert overUnit > 0;
    double selectionProbability = overUnit / (double) output.nonZeroElements();
    for (Integer activeLTUIndex : output) {
      if (random.nextFloat() > selectionProbability)
        continue;
      LTU ltu = ltus[activeLTUIndex];
      if (ltu == null || !(ltu instanceof LTUAdaptiveDensity))
        continue;
      ((LTUAdaptiveDensity) ltu).increaseDensity(random, obs);
    }
  }
}