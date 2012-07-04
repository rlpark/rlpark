package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
  private RealVector prototype = null;
  private final Thread thread;
  private final Map<Class<?>, Stack<MutableVector[]>> classToStacks = new HashMap<Class<?>, Stack<MutableVector[]>>();
  private final Stack<AllocatedBuffer> stackedBuffers = new Stack<AllocatedBuffer>();
  private MutableVector[] buffers;
  private int lastAllocation;

  public ThreadVectorPool() {
    this.thread = Thread.currentThread();
  }

  public void setPrototype(RealVector x) {
    if (buffers != null) {
      stackedBuffers.push(new AllocatedBuffer(prototype, buffers, lastAllocation));
      buffers = null;
      lastAllocation = -2;
      this.prototype = null;
    }
    Class<?> vectorClass = x.getClass();
    Stack<MutableVector[]> stack = classToStacks.get(vectorClass);
    if (stack == null) {
      stack = new Stack<MutableVector[]>();
      classToStacks.put(vectorClass, stack);
    }
    buffers = stack.isEmpty() ? new MutableVector[1] : stack.pop();
    this.prototype = x;
    lastAllocation = -1;
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
    Class<?> vectorClass = prototype.getClass();
    Stack<MutableVector[]> stack = classToStacks.get(vectorClass);
    stack.push(buffers);
    if (stackedBuffers.isEmpty()) {
      buffers = null;
      prototype = null;
      lastAllocation = -2;
    } else {
      AllocatedBuffer allocated = stackedBuffers.pop();
      buffers = allocated.buffers;
      prototype = allocated.prototype;
      lastAllocation = allocated.lastAllocation;
    }
  }
}
