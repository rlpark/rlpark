package rlpark.plugin.rltoys.algorithms.representations.ltu.units;

import java.io.Serializable;

public interface LTU extends Serializable {
  int index();

  int[] inputs();

  void updateSum(double[] inputVector);

  boolean updateActivation();

  LTU newLTU(int ltuIndex, int[] inputs, byte[] weights);

  boolean isActive();
}
