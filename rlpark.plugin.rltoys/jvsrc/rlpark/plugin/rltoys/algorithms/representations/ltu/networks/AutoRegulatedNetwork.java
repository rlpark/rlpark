package rlpark.plugin.rltoys.algorithms.representations.ltu.networks;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTUAdaptiveDensity;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class AutoRegulatedNetwork extends RandomNetwork {
  private static final long serialVersionUID = 1847556584654367004L;
  public final int minUnitActive;
  public final int maxUnitActive;
  public final double minDensity;
  public final double maxDensity;
  private final Random random;
  private int missingUnit;
  private int overUnit;

  public AutoRegulatedNetwork(Random random, int inputSize, int outputSize, double minDensity, double maxDensity) {
    super(inputSize, outputSize);
    this.random = random;
    this.minDensity = minDensity;
    this.maxDensity = maxDensity;
    this.minUnitActive = (int) (minDensity * outputSize);
    this.maxUnitActive = (int) (maxDensity * outputSize);
  }

  @Override
  protected void postProjection(BinaryVector obs) {
    missingUnit = 0;
    overUnit = 0;
    int nbActive = output.nonZeroElements();
    if (nbActive > maxUnitActive)
      decreaseDensity(obs);
    if (nbActive < minUnitActive)
      increaseDensity(obs);
    super.postProjection(obs);
  }

  private void increaseDensity(BinaryVector obs) {
    missingUnit = minUnitActive - output.nonZeroElements();
    Set<LTUAdaptiveDensity> couldHaveAgree = buildCouldHaveAgreeUnits(obs);
    assert missingUnit > 0;
    double selectionProbability = Math.min(1.0, missingUnit / (double) couldHaveAgree.size());
    for (LTUAdaptiveDensity ltu : couldHaveAgree) {
      if (random.nextFloat() > selectionProbability)
        continue;
      ltu.increaseDensity(random, denseInputVector);
    }
  }

  private Set<LTUAdaptiveDensity> buildCouldHaveAgreeUnits(BinaryVector obs) {
    Set<LTUAdaptiveDensity> couldHaveAgree = new LinkedHashSet<LTUAdaptiveDensity>();
    for (int activeInput : obs.getActiveIndexes()) {
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
    for (int activeLTUIndex : output.getActiveIndexes()) {
      if (random.nextFloat() > selectionProbability)
        continue;
      LTU ltu = ltus[activeLTUIndex];
      if (ltu == null || !(ltu instanceof LTUAdaptiveDensity))
        continue;
      ((LTUAdaptiveDensity) ltu).decreaseDensity(random, denseInputVector);
    }
  }
}
