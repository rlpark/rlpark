package rlpark.plugin.rltoys.math.vector.implementations;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class PVector extends AbstractVector implements DenseVector, MonitorContainer {
  private static final long serialVersionUID = -3114589590234820246L;

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


  @Override
  public MutableVector set(RealVector other, int start) {
    if (other instanceof DenseVector)
      return set(((DenseVector) other).accessData(), start);
    for (int i = 0; i < other.getDimension(); i++)
      data[i] = other.getEntry(i);
    return this;
  }

  @Override
  public MutableVector set(RealVector other) {
    return set(other, 0);
  }

  public PVector set(double[] v, int start) {
    System.arraycopy(v, 0, data, start, v.length);
    return this;
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
    if (other instanceof VectorNull)
      return 0;
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
  public PVector set(double d) {
    Arrays.fill(data, d);
    return this;
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
  public MutableVector ebeDivideToSelf(RealVector v) {
    for (int i = 0; i < data.length; i++) {
      double value = data[i];
      if (value != 0)
        data[i] = value / v.getEntry(i);
    }
    return this;
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
  public PVector addToSelf(double factor, RealVector vect) {
    if (vect instanceof BinaryVector) {
      addBinaryToSelf(factor, (BinaryVector) vect);
      return this;
    }
    if (vect instanceof SVector) {
      ((SVector) vect).addSelfTo(factor, data);
      return this;
    }
    for (int i = 0; i < vect.getDimension(); i++)
      data[i] += factor * vect.getEntry(i);
    return this;
  }

  private void addBinaryToSelf(double factor, BinaryVector vect) {
    int[] activeIndexes = vect.getActiveIndexes();
    int nbActives = vect.nonZeroElements();
    for (int i = 0; i < nbActives; i++)
      data[activeIndexes[i]] += factor;
  }

  @Override
  public String toString() {
    return Arrays.toString(data);
  }

  @Override
  public PVector clear() {
    Arrays.fill(data, 0);
    return this;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("l1Norm", new Monitored() {
      @Override
      public double monitoredValue() {
        return Vectors.l1Norm(PVector.this);
      }
    });
    monitor.add("infNorm", new Monitored() {
      @Override
      public double monitoredValue() {
        return Vectors.infNorm(PVector.this);
      }
    });
  }

  @Override
  public double sum() {
    double sum = 0;
    for (double v : data)
      sum += v;
    return sum;
  }

  public double min() {
    double min = Double.MAX_VALUE;
    for (double v : data)
      if (v < min)
        min = v;
    return min;
  }
}