package rlpark.plugin.rltoys.math.vector.implementations;

import rlpark.plugin.rltoys.math.vector.MutableVector;

public class SSortedVector extends SVector {
  private static final long serialVersionUID = -4937311063174913162L;

  public SSortedVector(int size) {
    super(size);
  }

  public SSortedVector(SVector other) {
    this(other.size);
    for (int i = 0; i < other.nbActive; i++)
      setEntry(other.activeIndexes[i], other.values[i]);
  }

  public SSortedVector(BVector other, double value) {
    super(other, value);
  }

  @Override
  protected void updateEntry(int index, double value, int position) {
    removeEntry(position, index);
    insertEntry(index, value);
  }

  private void removeEntries(int startPosition, int length) {
    final int endPosition = startPosition + length;
    for (int position = startPosition; position < endPosition; position++)
      indexesPosition[activeIndexes[position]] = -1;
    for (int position = endPosition; position < nbActive; position++)
      indexesPosition[activeIndexes[position]] -= length;
    System.arraycopy(activeIndexes, endPosition, activeIndexes, startPosition, nbActive - endPosition);
    System.arraycopy(values, endPosition, values, startPosition, nbActive - endPosition);
    nbActive -= length;
  }

  @Override
  protected void removeEntry(int indexPosition, int index) {
    removeEntries(indexPosition, 1);
  }

  public void removeTail(int n) {
    removeEntries(0, n);
  }

  @Override
  public SVector copy() {
    return new SSortedVector(this);
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SSortedVector(size);
  }
}
