package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;
import java.util.Iterator;

import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import rlpark.plugin.rltoys.utils.NotImplemented;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class PVector extends AbstractVector implements DenseVector {
  private static final long serialVersionUID = -3114589590234820246L;

  private static class PVectorEntry implements VectorEntry {
    private int current;
    private final double[] values;

    public PVectorEntry(double[] values) {
      this.values = values;
    }

    @Override
    public int index() {
      return current;
    }

    @Override
    public double value() {
      return values[current];
    }

    public void update(int current) {
      this.current = current;
    }
  }

  private class PVectorIterator implements Iterator<VectorEntry> {
    private int current;
    private final PVectorEntry entry;

    protected PVectorIterator() {
      current = 0;
      entry = new PVectorEntry(accessData());
    }

    @Override
    public boolean hasNext() {
      return current < size;
    }

    @Override
    public VectorEntry next() {
      entry.update(current++);
      return entry;
    }

    @Override
    public void remove() {
      throw new NotImplemented();
    }
  }

  final public double[] data;

  public PVector(int m) {
    super(m);
    data = new double[m];
  }

  public PVector(double... v) {
    this(v, false);
  }

  public PVector(double[] d, boolean copyArray) {
    super(d.length);
    data = copyArray ? d.clone() : d;
  }

  public PVector(RealVector v) {
    super(v.getDimension());
    data = new double[v.getDimension()];
    for (int i = 0; i < data.length; ++i)
      data[i] = v.getEntry(i);
  }

  public void set(RealVector other) {
    if (other instanceof DenseVector)
      set(((DenseVector) other).accessData());
    for (VectorEntry entry : other)
      data[entry.index()] = entry.value();
  }

  public PVector set(double[] v) {
    System.arraycopy(v, 0, data, 0, size);
    return this;
  }

  protected PVector newInstance(double[] data) {
    return new PVector(data, false);
  }

  @Override
  public PVector copy() {
    return new PVector(Arrays.copyOf(data, size));
  }

  @Override
  public PVector subtractToSelf(RealVector other) {
    if (other instanceof SparseVector) {
      ((SparseVector) other).subtractSelfTo(data);
      return this;
    }
    double[] o = ((DenseVector) other).accessData();
    for (int i = 0; i < data.length; i++)
      data[i] -= o[i];
    return this;
  }

  @Override
  public MutableVector addToSelf(RealVector other) {
    if (other instanceof SparseVector) {
      ((SparseVector) other).addSelfTo(data);
      return this;
    }
    double[] o = ((DenseVector) other).accessData();
    for (int i = 0; i < data.length; i++)
      data[i] += o[i];
    return this;
  }

  @Override
  public double dotProduct(RealVector other) {
    if (other instanceof SparseVector)
      return ((SparseVector) other).dotProduct(data);
    double result = 0;
    double[] o = ((PVector) other).data;
    for (int i = 0; i < data.length; i++)
      result += data[i] * o[i];
    return result;
  }

  @Override
  public double getEntry(int i) {
    return data[i];
  }

  @Override
  public MutableVector newInstance(int size) {
    return new PVector(size);
  }

  @Override
  public void set(double d) {
    Arrays.fill(data, d);
  }

  @Override
  public void setEntry(int i, double d) {
    data[i] = d;
  }

  @Override
  public MutableVector mapMultiplyToSelf(double d) {
    for (int i = 0; i < data.length; i++)
      data[i] *= d;
    return this;
  }

  @Override
  public double[] accessData() {
    return data;
  }

  public void addToSelf(double[] array) {
    assert array.length == size;
    for (int i = 0; i < array.length; i++)
      data[i] += array[i];
  }

  @Override
  public MutableVector ebeMultiplyToSelf(RealVector v) {
    if (v instanceof PVector)
      return ebeMultiplyToSelf(((PVector) v).data);
    for (int i = 0; i < data.length; i++)
      data[i] *= v.getEntry(i);
    return this;
  }

  private MutableVector ebeMultiplyToSelf(double[] other) {
    for (int i = 0; i < other.length; i++)
      data[i] *= other[i];
    return this;
  }

  @Override
  public MutableVector copyAsMutable() {
    return copy();
  }

  @Override
  public Iterator<VectorEntry> iterator() {
    return new PVectorIterator();
  }

  public PVector addToSelf(double factor, RealVector vect) {
    if (vect instanceof SVector)
      ((SVector) vect).addSelfTo(factor, data);
    else
      for (VectorEntry entry : vect)
        data[entry.index()] += factor * entry.value();
    return this;
  }

  @Override
  public String toString() {
    return Arrays.toString(data);
  }
}