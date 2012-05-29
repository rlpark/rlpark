package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ThreadVectorPool implements VectorPool {
  @Monitor
  int nbAllocation;
  private final MutableVector prototype;
  private final Thread thread;
  private MutableVector[] buffers;
  private int lastAllocation = -1;

  public ThreadVectorPool(RealVector prototype) {
    this.prototype = prototype.newInstance(prototype.getDimension());
    this.thread = Thread.currentThread();
    this.buffers = new MutableVector[10];
    releaseAll();
  }

  @Override
  public MutableVector newVector(int size) {
    return vectorCached(size).clear();
  }

  private MutableVector vectorCached(int size) {
    if (Thread.currentThread() != thread)
      throw new RuntimeException("Called from a wrong thread");
    lastAllocation++;
    if (lastAllocation == buffers.length)
      buffers = Arrays.copyOf(buffers, buffers.length * 2);
    MutableVector cached = buffers[lastAllocation];
    if (cached == null || cached.getDimension() != size) {
      nbAllocation++;
      cached = prototype.newInstance(size);
      buffers[lastAllocation] = cached;
    }
    return cached;
  }

  @Override
  public MutableVector newVector(RealVector v) {
    return vectorCached(v.getDimension()).set(v);
  }

  @Override
  public void releaseAll() {
    lastAllocation = -1;
  }
}
