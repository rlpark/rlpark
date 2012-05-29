package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class BVector extends AbstractVector implements BinaryVector {
  private static final long serialVersionUID = 5688026326299722364L;
  private int[] indexes;
  @Monitor
  private int nbActive = 0;

  public BVector(int size) {
    this(size, 0);
  }

  public BVector(int size, int allocated) {
    super(size);
    indexes = new int[allocated];
  }

  public BVector(BVector v) {
    this(v.size);
    indexes = Arrays.copyOf(v.indexes, v.indexes.length);
    nbActive = v.nbActive;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i : getActiveIndexes())
      data[i] += 1;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (int i : getActiveIndexes())
      result += data[i];
    return result;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int i : getActiveIndexes())
      data[i] -= 1;
  }

  @Override
  public SVector copyAsMutable() {
    return new SVector(this, 1);
  }

  @Override
  public BVector copy() {
    return new BVector(this);
  }

  @Override
  public double dotProduct(RealVector other) {
    double result = 0;
    for (int i : getActiveIndexes())
      result += other.getEntry(i);
    return result;
  }

  @Override
  public double getEntry(int i) {
    return search(i) >= 0 ? 1 : 0;
  }

  private int search(int i) {
    return Arrays.binarySearch(indexes, 0, nbActive, i);
  }

  @Override
  public MutableVector mapMultiply(double d) {
    SVector result = copyAsMutable();
    result.mapMultiplyToSelf(d);
    return result;
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public BVector clear() {
    nbActive = 0;
    return this;
  }

  @Override
  public void setOn(int index) {
    assert index < size;
    int searchResult = search(index);
    if (searchResult >= 0)
      return;
    int insertion = -searchResult - 1;
    nbActive++;
    int[] newActiveIndex = indexes;
    if (nbActive >= indexes.length) {
      int newLength = indexes.length > 0 ? indexes.length * 2 : 1;
      newActiveIndex = new int[newLength];
      System.arraycopy(indexes, 0, newActiveIndex, 0, insertion);
    }
    System.arraycopy(indexes, insertion, newActiveIndex, insertion + 1, nbActive - insertion - 1);
    newActiveIndex[insertion] = index;
    indexes = newActiveIndex;
  }

  private boolean canAppend(int index) {
    return nbActive == 0 || index > indexes[nbActive - 1];
  }

  @Override
  public String toString() {
    return Arrays.toString(getActiveIndexes());
  }

  @Override
  public int nonZeroElements() {
    return nbActive;
  }

  @Override
  final public int[] getActiveIndexes() {
    if (indexes.length > nbActive)
      indexes = Arrays.copyOf(indexes, nbActive);
    return indexes;
  }

  public void mergeSubVector(int start, BinaryVector other) {
    if (!canAppend(start))
      throw new RuntimeException("cannot append when other indexes are less than already active indexes");
    int[] otherIndexes = other.getActiveIndexes();
    assert otherIndexes.length == 0 || otherIndexes[otherIndexes.length - 1] + start < size;
    allocate(nbActive + otherIndexes.length);
    for (int otherIndex : otherIndexes) {
      indexes[nbActive] = otherIndex + start;
      nbActive++;
    }
  }

  public void removeEntry(int index) {
    int searchResult = search(index);
    if (searchResult < 0)
      return;
    System.arraycopy(indexes, searchResult + 1, indexes, searchResult, nbActive - searchResult - 1);
    nbActive--;
  }

  public void allocate(int allocation) {
    if (allocation <= indexes.length)
      return;
    indexes = Arrays.copyOf(indexes, allocation);
  }

  public void setOrderedIndexes(int[] orderedIndexes) {
    indexes = orderedIndexes.clone();
    nbActive = indexes.length;
  }

  public static BVector toBinary(double[] ds) {
    int[] is = new int[ds.length];
    for (int i = 0; i < is.length; i++)
      is[i] = (int) ds[i];
    return toBinary(is);
  }

  public static BVector toBinary(byte[] is) {
    BVector bobs = new BVector(is.length * Byte.SIZE);
    for (int i = 0; i < is.length; i++)
      for (int bi = 0; bi < Byte.SIZE; bi++) {
        int mask = 1 << bi;
        if ((is[i] & mask) != 0)
          bobs.setOn(i * Byte.SIZE + bi);
      }
    return bobs;
  }

  public static BVector toBinary(int[] is) {
    BVector bobs = new BVector(is.length * Integer.SIZE);
    for (int i = 0; i < is.length; i++)
      for (int bi = 0; bi < Integer.SIZE; bi++) {
        int mask = 1 << bi;
        if ((is[i] & mask) != 0)
          bobs.setOn(i * Integer.SIZE + bi);
      }
    return bobs;
  }

  public static BVector toBVector(int size, int[] orderedIndexes) {
    BVector result = new BVector(size);
    result.setOrderedIndexes(orderedIndexes);
    return result;
  }

  @Override
  public double[] accessData() {
    double[] data = new double[size];
    for (int i : getActiveIndexes())
      data[i] = 1.0;
    return data;
  }

  @Override
  public int[] nonZeroIndexes() {
    return indexes;
  }
}
