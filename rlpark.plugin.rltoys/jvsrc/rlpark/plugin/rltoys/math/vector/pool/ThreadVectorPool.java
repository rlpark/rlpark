package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Arrays;
import java.util.Stack;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ThreadVectorPool implements VectorPool {
  class AllocatedBuffer {
    final MutableVector[] buffers;
    final int lastAllocation;
    final RealVector prototype;

    AllocatedBuffer(RealVector prototype, MutableVector[] buffers, int lastAllocation) {
      this.prototype = prototype;
      this.buffers = buffers;
      this.lastAllocation = lastAllocation;
    }
  }

  @Monitor
  int nbAllocation;
  private final Thread thread;
  private final Stack<MutableVector[]> stackedVectors = new Stack<MutableVector[]>();
  private final Stack<AllocatedBuffer> stackedBuffers = new Stack<AllocatedBuffer>();
  private MutableVector[] buffers;
  private int lastAllocation;
  private final RealVector prototype;
  private final int dimension;

  public ThreadVectorPool(RealVector prototype, int dimension) {
    this.dimension = dimension;
    this.thread = Thread.currentThread();
    this.prototype = prototype;
  }

  public void allocate() {
    if (buffers != null) {
      stackedBuffers.push(new AllocatedBuffer(prototype, buffers, lastAllocation));
      buffers = null;
      lastAllocation = -2;
    }
    buffers = stackedVectors.isEmpty() ? new MutableVector[1] : stackedVectors.pop();
    lastAllocation = -1;
  }

  @Override
  public MutableVector newVector() {
    return vectorCached().clear();
  }

  private MutableVector vectorCached() {
    if (Thread.currentThread() != thread)
      throw new RuntimeException("Called from a wrong thread");
    lastAllocation++;
    if (lastAllocation == buffers.length)
      buffers = Arrays.copyOf(buffers, buffers.length * 2);
    MutableVector cached = buffers[lastAllocation];
    if (cached == null) {
      nbAllocation++;
      cached = prototype.newInstance(dimension);
      buffers[lastAllocation] = cached;
    }
    return cached;
  }

  @Override
  public MutableVector newVector(RealVector v) {
    assert dimension == v.getDimension();
    return vectorCached().set(v);
  }

  @Override
  public void releaseAll() {
    stackedVectors.push(buffers);
    if (stackedBuffers.isEmpty()) {
      buffers = null;
      lastAllocation = -2;
    } else {
      AllocatedBuffer allocated = stackedBuffers.pop();
      buffers = allocated.buffers;
      lastAllocation = allocated.lastAllocation;
    }
  }
}
