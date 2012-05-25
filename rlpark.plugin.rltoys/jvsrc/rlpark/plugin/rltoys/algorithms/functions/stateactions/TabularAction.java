package rlpark.plugin.rltoys.algorithms.functions.stateactions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class TabularAction implements StateToStateAction {
  private static final long serialVersionUID = 1705117400022134128L;
  private final Action[] actions;
  private final int vectorSize;
  private final BVector nullVector;
  private final double vectorNorm;

  public TabularAction(Action[] actions, double vectorNorm, int vectorSize) {
    this.actions = actions;
    this.vectorNorm = vectorNorm;
    this.vectorSize = vectorSize;
    this.nullVector = new BVector(vectorSize());
  }

  @Override
  public int vectorSize() {
    return vectorSize * actions.length;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null)
      return nullVector;
    if (s instanceof BinaryVector)
      return stateAction((BinaryVector) s, a);
    MutableVector phi_sa = s.newInstance(vectorSize());
    for (int a_i = 0; a_i < actions.length; a_i++)
      if (actions[a_i] == a) {
        int offset = vectorSize * a_i;
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
        int offset = vectorSize * i;
        int[] indexes = phi_sa.getActiveIndexes();
        for (int j = 0; j < indexes.length; j++)
          indexes[j] += offset;
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
