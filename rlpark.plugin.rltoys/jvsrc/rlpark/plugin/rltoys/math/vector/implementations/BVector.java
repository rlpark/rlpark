package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class BVector extends AbstractVector implements BinaryVector {
  private static final long serialVersionUID = 5688026326299722364L;
  public int[] activeIndexes;
  private final int[] indexesPosition;
  @Monitor
  int nbActive = 0;

  public BVector(int size) {
    super(size);
    activeIndexes = new int[10];
    indexesPosition = new int[size];
    Arrays.fill(indexesPosition, -1);
  }

  public BVector(BVector other) {
    this(other.getDimension());
    set(other);
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int position = 0; position < nbActive; position++)
      data[activeIndexes[position]] += 1;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (int position = 0; position < nbActive; position++)
      result += data[activeIndexes[position]];
    return result;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int position = 0; position < nbActive; position++)
      data[activeIndexes[position]] -= 1;
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
    double result = 0.0;
    for (int position = 0; position < nbActive; position++)
      result += other.getEntry(activeIndexes[position]);
    return result;
  }

  @Override
  public double getEntry(int index) {
    return indexesPosition[index] != -1 ? 1 : 0;
  }

  @Override
  public MutableVector mapMultiply(double d) {
    return copyAsMutable().mapMultiplyToSelf(d);
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public BVector clear() {
    for (int i = 0; i < nbActive; i++)
      indexesPosition[activeIndexes[i]] = -1;
    nbActive = 0;
    return this;
  }

  protected void appendEntry(int index) {
    allocate(nbActive + 1);
    activeIndexes[nbActive] = index;
    indexesPosition[index] = nbActive;
    nbActive++;
  }

  protected void allocate(int sizeRequired) {
    if (activeIndexes.length >= sizeRequired)
      return;
    int newCapacity = (sizeRequired * 3) / 2 + 1;
    activeIndexes = Arrays.copyOf(activeIndexes, newCapacity);
  }

  protected void removeEntry(int position, int index) {
    assert position >= 0;
    swapEntry(nbActive - 1, position);
    indexesPosition[activeIndexes[nbActive - 1]] = -1;
    nbActive--;
  }

  private void swapEntry(int positionA, int positionB) {
    final int indexA = activeIndexes[positionA];
    final int indexB = activeIndexes[positionB];
    indexesPosition[indexA] = positionB;
    indexesPosition[indexB] = positionA;
    activeIndexes[positionA] = indexB;
    activeIndexes[positionB] = indexA;
  }

  public void removeEntry(int index) {
    int position = indexesPosition[index];
    if (position >= 0)
      removeEntry(position, index);
  }

  @Override
  public void setOn(int index) {
    assert index >= 0 && index < getDimension();
    int position = indexesPosition[index];
    if (position == -1)
      appendEntry(index);
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
    if (activeIndexes.length > nbActive)
      activeIndexes = Arrays.copyOf(activeIndexes, nbActive);
    return activeIndexes;
  }

  public void mergeSubVector(int start, BinaryVector other) {
    allocate(nbActive + other.nonZeroElements());
    for (int otherIndex : other.getActiveIndexes())
      setOn(start + otherIndex);
  }

  public void set(BinaryVector source) {
    clear();
    mergeSubVector(0, source);
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
    for (int i : orderedIndexes)
      result.setOn(i);
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
    return activeIndexes;
  }

  @Override
  public double sum() {
    return nonZeroElements();
  }
}
