package rlpark.plugin.rltoys.algorithms.representations.ltu.units;

import java.util.Random;

public interface LTUAdaptiveDensity extends LTU {
  void increaseDensity(Random random, double[] inputVector);

  void decreaseDensity(Random random, double[] inputVector);
}
