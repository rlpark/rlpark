package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class ThreadVectorPool implements VectorPool {
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
    if (Thread.currentThread() != thread)
      throw new RuntimeException("Called from a wrong thread");
    lastAllocation++;
    if (lastAllocation == buffers.length)
      buffers = Arrays.copyOf(buffers, buffers.length * 2);
    if (buffers[lastAllocation] == null)
      buffers[lastAllocation] = prototype.newInstance(size);
    return buffers[lastAllocation];
  }

  @Override
  public MutableVector newVector(RealVector v) {
    return newVector(v.getDimension()).set(v);
  }

  @Override
  public void releaseAll() {
    for (int i = 0; i <= lastAllocation; i++)
      buffers[lastAllocation].clear();
    lastAllocation = -1;
  }
}
