package rlpark.plugin.rltoys.algorithms.representations.ltu.internal;

import java.io.Serializable;
import java.util.Arrays;

import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;

public class LTUUpdated implements Serializable {
  private static final long serialVersionUID = -8496340357389642735L;
  public final boolean[] updated;
  private int nbUnitUpdated;

  public LTUUpdated(int nbLTU) {
    updated = new boolean[nbLTU];
  }

  final public void updateLTUSum(int index, LTU ltu, double[] denseInputVector) {
    if (markLTU(index))
      ltu.updateSum(denseInputVector);
  }

  synchronized private boolean markLTU(int index) {
    if (updated[index])
      return false;
    nbUnitUpdated++;
    updated[index] = true;
    return true;
  }

  synchronized public void clean() {
    Arrays.fill(updated, false);
    nbUnitUpdated = 0;
  }

  public int nbUnitUpdated() {
    return nbUnitUpdated;
  }
}
