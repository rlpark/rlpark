package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;
import java.util.Iterator;

import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseRealVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SVector extends AbstractVector implements SparseRealVector {
  private static final long serialVersionUID = -3324707947990480491L;

  private class BufferedSVector {
    final int[] indexes;
    final double[] values;
    final int nbActive;

    BufferedSVector(SVector vector) {
      indexes = Arrays.copyOf(vector.indexes, vector.indexes.length);
      values = Arrays.copyOf(vector.values, vector.values.length);
      nbActive = vector.nbActive;
    }
  }

  private class SVectorEntry implements VectorEntry {
    private int current;

    @Override
    public int index() {
      return indexes[current];
    }

    @Override
    public double value() {
      return values[current];
    }

    public void update(int current) {
      this.current = current;
    }
  }

  private class SVectorIterator implements Iterator<VectorEntry> {
    private int current;
    private final SVectorEntry entry;
    private boolean removed;

    @SuppressWarnings("synthetic-access")
    protected SVectorIterator() {
      current = -1;
      nbActive = SVector.this.nbActive;
      entry = new SVectorEntry();
    }

    @Override
    public boolean hasNext() {
      return current < nbActive - 1;
    }

    @Override
    public VectorEntry next() {
      if (!removed) {
        current++;
        entry.update(current);
      } else
        removed = false;
      return entry;
    }

    @Override
    public void remove() {
      removeExistingEntry(current);
      removed = true;
    }
  }

  public int[] indexes;
  public double[] values;
  @Monitor
  int nbActive = 0;

  public SVector(int size) {
    this(size, 0);
  }

  public SVector(int size, int allocated) {
    super(size);
    indexes = new int[allocated];
    values = new double[allocated];
  }

  public SVector(SVector other) {
    this(other.size, other.nonZeroElements());
    System.arraycopy(other.indexes, 0, indexes, 0, indexes.length);
    System.arraycopy(other.values, 0, values, 0, values.length);
    nbActive = other.nbActive;
  }

  public SVector(BVector other, double value) {
    this(other.size, other.nonZeroElements());
    System.arraycopy(other.activeIndexes(), 0, indexes, 0, indexes.length);
    Arrays.fill(values, value);
    nbActive = indexes.length;
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

  public void removeExistingEntry(int entryIndex) {
    System.arraycopy(indexes, entryIndex + 1, indexes, entryIndex, nbActive - entryIndex - 1);
    System.arraycopy(values, entryIndex + 1, values, entryIndex, nbActive - entryIndex - 1);
    nbActive--;
  }

  @Override
  public void setEntry(int index, double value) {
    int searchResult = search(index);
    if (value == 0.0) {
      if (searchResult < 0)
        return;
      removeExistingEntry(searchResult);
      return;
    }
    int position = searchResult;
    if (position < 0) {
      position = notFoundToPosition(position);
      insertElementAtPosition(position, index, value);
    } else
      values[position] = value;
  }

  static public int notFoundToPosition(int searchResult) {
    return -searchResult - 1;
  }

  public void insertElementAtPosition(int insertion, int index, double value) {
    nbActive++;
    int[] newActiveIndex = indexes;
    double[] newActiveValues = values;
    if (nbActive >= indexes.length) {
      int newLength = indexes.length > 0 ? indexes.length * 2 : 1;
      newActiveIndex = new int[newLength];
      newActiveValues = new double[newLength];
      System.arraycopy(indexes, 0, newActiveIndex, 0, insertion);
      System.arraycopy(values, 0, newActiveValues, 0, insertion);
    }
    System.arraycopy(indexes, insertion, newActiveIndex, insertion + 1, nbActive - insertion - 1);
    System.arraycopy(values, insertion, newActiveValues, insertion + 1, nbActive - insertion - 1);
    newActiveIndex[insertion] = index;
    newActiveValues[insertion] = value;
    indexes = newActiveIndex;
    values = newActiveValues;
  }

  private int search(int index) {
    return searchFrom(0, index);
  }

  public int searchFrom(int start, int index) {
    return Arrays.binarySearch(indexes, start, nbActive, index);
  }

  public MutableVector addToSelf(BVector other) {
    if (other.nonZeroElements() == 0)
      return this;
    final BufferedSVector thisB = new BufferedSVector(this);
    int[] otherIndexes = other.activeIndexes();
    int i = 0, j = 0;
    clear();
    while (i < thisB.nbActive || j < otherIndexes.length) {
      if (j < otherIndexes.length && (i == thisB.nbActive || thisB.indexes[i] > otherIndexes[j])) {
        append(otherIndexes[j], 1.0);
        j++;
      } else if (j == otherIndexes.length || thisB.indexes[i] < otherIndexes[j]) {
        append(thisB.indexes[i], thisB.values[i]);
        i++;
      } else {
        append(thisB.indexes[i], thisB.values[i] + 1.0);
        i++;
        j++;
      }
    }
    return this;
  }

  private MutableVector addToSelf(SVector other, final double otherMultiplier) {
    if (other.nbActive == 0)
      return this;
    BufferedSVector thisB = new BufferedSVector(this);
    int[] otherIndexes = other.indexes;
    double[] otherValues = other.values;
    int i = 0, j = 0;
    clear();
    while (i < thisB.nbActive || j < other.nbActive) {
      if (j < otherIndexes.length && (i == thisB.nbActive || thisB.indexes[i] > otherIndexes[j])) {
        append(otherIndexes[j], otherMultiplier * otherValues[j]);
        j++;
      } else if (j == otherIndexes.length || thisB.indexes[i] < otherIndexes[j]) {
        append(thisB.indexes[i], thisB.values[i]);
        i++;
      } else {
        append(thisB.indexes[i], thisB.values[i] + otherMultiplier * otherValues[j]);
        i++;
        j++;
      }
    }
    return this;
  }

  @Override
  public MutableVector addToSelf(RealVector other) {
    if (other instanceof SVector)
      return addToSelf((SVector) other, 1);
    if (other instanceof BVector)
      return addToSelf((BVector) other);
    int thisPosition = 0;
    for (VectorEntry entry : other) {
      int position = addEntryToSelf(thisPosition, entry.index(), entry.value());
      thisPosition = position + 1;
    }
    return this;
  }

  private int addEntryToSelf(int positionSearchFrom, int otherIndex, double otherValue) {
    int search = searchFrom(positionSearchFrom, otherIndex);
    int position = search;
    if (position < 0) {
      position = notFoundToPosition(search);
      insertElementAtPosition(position, otherIndex, otherValue);
    } else
      values[position] += otherValue;
    return position;
  }

  @Override
  public MutableVector subtractToSelf(RealVector other) {
    if (other instanceof SVector)
      return addToSelf((SVector) other, -1);
    int thisPosition = 0;
    for (VectorEntry entry : other) {
      int otherIndex = entry.index();
      int search = searchFrom(thisPosition, otherIndex);
      int position = search;
      if (position < 0) {
        position = notFoundToPosition(search);
        insertElementAtPosition(position, otherIndex, -entry.value());
      } else
        values[position] -= entry.value();
      thisPosition = position + 1;
    }
    return this;
  }

  public MutableVector ebeMultiplyToSelf(SVector other) {
    int[] thisIndexes = Arrays.copyOf(this.indexes, this.indexes.length);
    double[] thisValues = Arrays.copyOf(this.values, this.values.length);
    int thisNbActive = this.nbActive;
    int i = 0, j = 0;
    clear();
    while (i < thisNbActive && j < other.nbActive) {
      if (thisIndexes[i] > other.indexes[j])
        j++;
      else if (thisIndexes[i] < other.indexes[j])
        i++;
      else {
        insertElementAtPosition(nbActive, thisIndexes[i], thisValues[i] * other.values[j]);
        i++;
        j++;
      }
    }
    return this;
  }

  public MutableVector ebeMultiplyToSelf(DenseVector other) {
    double[] otherValues = other.accessData();
    for (int i = 0; i < nbActive; i++)
      values[i] *= otherValues[indexes[i]];
    return this;
  }

  @Override
  public MutableVector ebeMultiplyToSelf(RealVector other) {
    if (other instanceof SVector)
      return ebeMultiplyToSelf((SVector) other);
    if (other instanceof DenseVector)
      return ebeMultiplyToSelf((PVector) other);

    int[] thisIndexes = Arrays.copyOf(this.indexes, this.indexes.length);
    double[] thisValues = Arrays.copyOf(this.values, this.values.length);
    int thisNbActive = this.nbActive;
    clear();
    for (int i = 0; i < thisNbActive; i++) {
      int index = thisIndexes[i];
      append(index, thisValues[i] * other.getEntry(index));
    }
    return this;
  }

  private void append(int index, double value) {
    if (value == 0.0)
      return;
    insertElementAtPosition(nbActive, index, value);
  }

  @Override
  public MutableVector mapMultiplyToSelf(double d) {
    if (d == 0.0) {
      clear();
      return this;
    }
    for (int i = 0; i < nbActive; i++)
      values[i] *= d;
    return this;
  }

  @Override
  public double getEntry(int i) {
    final int position = search(i);
    return position >= 0 ? values[position] : 0;
  }

  @Override
  public double dotProduct(RealVector other) {
    double sum = 0;
    for (int i = 0; i < nbActive; i++)
      sum += values[i] * other.getEntry(indexes[i]);
    return sum;
  }

  @Override
  public double dotProduct(final double[] data) {
    double sum = 0;
    for (int i = 0; i < nbActive; i++)
      sum += values[i] * data[indexes[i]];
    return sum;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] += values[i];
  }

  public void addSelfTo(double selfFactor, double[] data) {
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] += selfFactor * values[i];
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] -= values[i];
  }

  @Override
  public int nonZeroElements() {
    return nbActive;
  }

  @Override
  public void clear() {
    nbActive = 0;
  }

  @Override
  public Iterator<VectorEntry> iterator() {
    return new SVectorIterator();
  }

  public int[] activeIndexes() {
    if (indexes.length > nbActive) {
      indexes = Arrays.copyOf(indexes, nbActive);
      values = Arrays.copyOf(values, nbActive);
    }
    return indexes;
  }

  @Override
  public double[] accessData() {
    double[] data = new double[size];
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] = values[i];
    return data;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < nbActive; i++) {
      result.append(indexes[i]);
      result.append(":");
      result.append(values[i]);
      if (i < nbActive - 1)
        result.append(", ");
    }
    result.append("]");
    return result.toString();
  }

  public SVector set(double d) {
    Arrays.fill(values, 0, nbActive, d);
    return this;
  }
}
