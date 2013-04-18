package rlpark.plugin.rltoys.algorithms.functions.stateactions;

import java.util.Map;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.Actions;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class TabularAction implements StateToStateAction, Cloneable {
  private static final long serialVersionUID = 1705117400022134128L;
  private final Action[] actions;
  private final int stateVectorSize;
  private BVector nullVector;
  private final double vectorNorm;
  private boolean includeActiveFeature = false;
  private RealVector buffer;
  private final Map<Action, Integer> actionToIndex;

  public TabularAction(Action[] actions, double vectorNorm, int vectorSize) {
    this.actions = actions;
    this.vectorNorm = vectorNorm + 1;
    this.stateVectorSize = vectorSize;
    this.nullVector = new BVector(vectorSize());
    actionToIndex = Actions.createActionIntMap(actions);
  }

  protected int atoi(Action a) {
    return actionToIndex.get(a);
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
    if (buffer == null)
      buffer = (s instanceof BinaryVector) ? new BVector(vectorSize()) : s.newInstance(vectorSize());
    int offset = atoi(a) * stateVectorSize;
    if (s instanceof BinaryVector)
      return stateAction((BinaryVector) s, offset);
    MutableVector phi_sa = (MutableVector) buffer;
    phi_sa.clear();
    if (includeActiveFeature)
      phi_sa.setEntry(vectorSize() - 1, 1);
    for (int s_i = 0; s_i < s.getDimension(); s_i++)
      phi_sa.setEntry(s_i + offset, s.getEntry(s_i));
    return phi_sa;
  }

  private RealVector stateAction(BinaryVector s, int offset) {
    BVector phi_sa = (BVector) buffer;
    phi_sa.clear();
    phi_sa.mergeSubVector(offset, s);
    if (includeActiveFeature)
      phi_sa.setOn(phi_sa.getDimension() - 1);
    return phi_sa;
  }

  public Action[] actions() {
    return actions;
  }

  @Override
  public double vectorNorm() {
    return vectorNorm;
  }

  @Override
  public StateToStateAction clone() throws CloneNotSupportedException {
    return new TabularAction(actions, vectorNorm - 1, stateVectorSize);
  }
}
