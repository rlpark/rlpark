package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseRealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SVector extends AbstractVector implements SparseRealVector, MonitorContainer {
  private static final long serialVersionUID = -3324707947990480491L;
  public int[] activeIndexes;
  public double[] values;
  public int[] indexesPosition;
  @Monitor
  int nbActive = 0;

  public SVector(int size) {
    super(size);
    values = new double[10];
    activeIndexes = new int[10];
    indexesPosition = new int[size];
    Arrays.fill(indexesPosition, -1);
  }

  public SVector(SVector other) {
    this(other.getDimension());
    set(other);
  }

  public SVector(BVector other, double value) {
    this(other.size);
    if (value == 0)
      return;
    setFromBVector(other, value);
  }

  @Override
  public SVector copy() {
    return new SVector(this);
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public MutableVector copyAsMutable() {
    return copy();
  }

  @Override
  public MutableVector addToSelf(RealVector other) {
    return addToSelf(1, other);
  }

  private MutableVector addToSelf(SVector other, double factor) {
    for (int position = 0; position < other.nbActive; position++) {
      final int index = other.activeIndexes[position];
      setNonZeroEntry(index, getEntry(index) + factor * other.values[position]);
    }
    return this;
  }

  private MutableVector addToSelf(BinaryVector other, double factor) {
    int[] nonNullIndexes = other.getActiveIndexes();
    for (int index : nonNullIndexes)
      setNonZeroEntry(index, getEntry(index) + factor);
    return this;
  }

  @Override
  public MutableVector addToSelf(double factor, RealVector other) {
    if (other instanceof SVector)
      return addToSelf((SVector) other, factor);
    if (other instanceof BinaryVector)
      return addToSelf((BinaryVector) other, factor);
    for (int i = 0; i < other.getDimension(); i++)
      setEntry(i, getEntry(i) + factor * other.getEntry(i));
    return this;
  }

  @Override
  public MutableVector subtractToSelf(RealVector other) {
    return addToSelf(-1, other);
  }

  @Override
  public MutableVector mapMultiplyToSelf(double factor) {
    if (factor == 0) {
      clear();
      return this;
    }
    for (int position = 0; position < nbActive; position++)
      values[position] *= factor;
    return this;
  }

  @Override
  public void removeEntry(int index) {
    int position = indexesPosition[index];
    if (position != -1)
      removeEntry(position, index);
  }

  @Override
  public void setEntry(int index, double value) {
    if (value == 0)
      removeEntry(index);
    else
      setNonZeroEntry(index, value);
  }

  private void setNonZeroEntry(int index, double value) {
    int position = indexesPosition[index];
    if (position != -1)
      updateEntry(index, value, position);
    else
      insertEntry(index, value);
  }

  protected void insertEntry(int index, double value) {
    appendEntry(index, value);
  }

  protected void appendEntry(int index, double value) {
    allocate(nbActive + 1);
    activeIndexes[nbActive] = index;
    values[nbActive] = value;
    indexesPosition[index] = nbActive;
    nbActive++;
  }

  protected void allocate(int sizeRequired) {
    if (activeIndexes.length >= sizeRequired)
      return;
    int newCapacity = (sizeRequired * 3) / 2 + 1;
    activeIndexes = Arrays.copyOf(activeIndexes, newCapacity);
    values = Arrays.copyOf(values, newCapacity);
  }

  protected void updateEntry(int index, double value, int position) {
    values[position] = value;
  }

  protected void removeEntry(int position, int index) {
    swapEntry(nbActive - 1, position);
    indexesPosition[activeIndexes[nbActive - 1]] = -1;
    nbActive--;
  }

  private void swapEntry(int positionA, int positionB) {
    final int indexA = activeIndexes[positionA];
    final double valueA = values[positionA];
    final int indexB = activeIndexes[positionB];
    final double valueB = values[positionB];
    indexesPosition[indexA] = positionB;
    indexesPosition[indexB] = positionA;
    activeIndexes[positionA] = indexB;
    activeIndexes[positionB] = indexA;
    values[positionA] = valueB;
    values[positionB] = valueA;
  }

  @Override
  public MutableVector ebeDivideToSelf(RealVector other) {
    for (int position = 0; position < nbActive; position++) {
      final int index = activeIndexes[position];
      values[position] /= other.getEntry(index);
    }
    return this;
  }

  @Override
  public MutableVector ebeMultiplyToSelf(RealVector other) {
    int position = 0;
    while (position < nbActive) {
      final int index = activeIndexes[position];
      double value = values[position] * other.getEntry(index);
      if (value != 0) {
        values[position] = value;
        position++;
      } else
        removeEntry(position, index);
    }
    return this;
  }

  @Override
  public double getEntry(int index) {
    final int position = indexesPosition[index];
    return position != -1 ? values[position] : 0;
  }

  @Override
  public double[] accessData() {
    double[] result = new double[size];
    for (int position = 0; position < nbActive; position++) {
      final int index = activeIndexes[position];
      result[index] = values[position];
    }
    return result;
  }

  @Override
  public SVector clear() {
    for (int i = 0; i < nbActive; i++)
      indexesPosition[activeIndexes[i]] = -1;
    nbActive = 0;
    return this;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (int position = 0; position < nbActive; position++)
      result += data[activeIndexes[position]] * values[position];
    return result;
  }

  @Override
  public double dotProduct(RealVector other) {
    if (other instanceof SparseVector && ((SparseVector) other).nonZeroElements() < nonZeroElements())
      return other.dotProduct(this);
    double result = 0.0;
    for (int position = 0; position < nbActive; position++)
      result += other.getEntry(activeIndexes[position]) * values[position];
    return result;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int position = 0; position < nbActive; position++)
      data[activeIndexes[position]] += values[position];
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int position = 0; position < nbActive; position++)
      data[activeIndexes[position]] -= values[position];
  }

  public void addSelfTo(double factor, double[] data) {
    for (int position = 0; position < nbActive; position++)
      data[activeIndexes[position]] += factor * values[position];
  }

  public int[] getActiveIndexes() {
    return Arrays.copyOf(activeIndexes, nbActive);
  }

  @Override
  public int nonZeroElements() {
    return nbActive;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("[");
    for (int position = 0; position < nbActive; position++) {
      result.append(activeIndexes[position]);
      result.append(":");
      result.append(values[position]);
      if (position < nbActive - 1)
        result.append(", ");
    }
    result.append("]");
    return result.toString();
  }

  @Override
  public SVector set(RealVector other) {
    if (other instanceof SVector)
      return set((SVector) other);
    if (other instanceof BVector)
      return setFromBVector((BVector) other, 1.0);
    clear();
    for (int i = 0; i < other.getDimension(); i++)
      setEntry(i, other.getEntry(i));
    return this;
  }

  private SVector set(SVector other) {
    clear();
    allocate(other.nbActive);
    nbActive = other.nbActive;
    System.arraycopy(other.activeIndexes, 0, activeIndexes, 0, nbActive);
    System.arraycopy(other.values, 0, values, 0, nbActive);
    for (int position = 0; position < nbActive; position++)
      indexesPosition[activeIndexes[position]] = position;
    return this;
  }


  @Override
  public MutableVector set(RealVector other, int start) {
    for (int i = 0; i < other.getDimension(); i++)
      setEntry(start + i, other.getEntry(i));
    return this;
  }

  private SVector setFromBVector(BVector other, double value) {
    clear();
    allocate(other.nonZeroElements());
    for (int i = 0; i < other.nonZeroElements(); i++)
      setNonZeroEntry(other.activeIndexes[i], value);
    return this;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("l1norm", new Monitored() {
      @Override
      public double monitoredValue() {
        return Vectors.l1Norm(SVector.this);
      }
    });
  }

  @Override
  public int[] nonZeroIndexes() {
    return activeIndexes;
  }

  @Override
  public double sum() {
    double sum = 0;
    for (int i = 0; i < nbActive; i++)
      sum += values[i];
    return sum;
  }
}
