package rlpark.plugin.rltoys.algorithms.functions.stateactions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class TabularAction implements StateToStateAction {
  private static final long serialVersionUID = 1705117400022134128L;
  private final Action[] actions;
  private final int stateVectorSize;
  private BVector nullVector;
  private final double vectorNorm;
  private boolean includeActiveFeature = false;

  public TabularAction(Action[] actions, double vectorNorm, int vectorSize) {
    this.actions = actions;
    this.vectorNorm = vectorNorm + 1;
    this.stateVectorSize = vectorSize;
    this.nullVector = new BVector(vectorSize());
  }

  public void includeActiveFeature() {
    includeActiveFeature = true;
    this.nullVector = new BVector(vectorSize());
  }

  @Override
  public int vectorSize() {
    int result = stateVectorSize * actions.length;
    if (includeActiveFeature)
      result += 1;
    return result;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null)
      return nullVector;
    if (s instanceof BinaryVector)
      return stateAction((BinaryVector) s, a);
    MutableVector phi_sa = s.newInstance(vectorSize());
    if (includeActiveFeature)
      phi_sa.setEntry(vectorSize() - 1, 1);
    for (int a_i = 0; a_i < actions.length; a_i++)
      if (actions[a_i] == a) {
        int offset = stateVectorSize * a_i;
        for (int s_i = 0; s_i < s.getDimension(); s_i++)
          phi_sa.setEntry(s_i + offset, s.getEntry(s_i));
        return phi_sa;
      }
    return null;
  }

  private RealVector stateAction(BinaryVector s, Action a) {
    BVector phi_sa = new BVector(vectorSize());
    phi_sa.setOrderedIndexes(s.getActiveIndexes());
    for (int i = 0; i < actions.length; i++)
      if (actions[i] == a) {
        int offset = stateVectorSize * i;
        int[] indexes = phi_sa.getActiveIndexes();
        for (int j = 0; j < phi_sa.nonZeroElements(); j++)
          indexes[j] += offset;
        if (includeActiveFeature)
          phi_sa.setOn(vectorSize() - 1);
        return phi_sa;
      }
    return null;
  }

  public Action[] actions() {
    return actions;
  }

  @Override
  public double vectorNorm() {
    return vectorNorm;
  }
}
