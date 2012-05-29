package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rlpark.plugin.rltoys.math.vector.RealVector;

public class VectorPools {
  static public Map<Thread, VectorPool> threadToPool = Collections.synchronizedMap(new HashMap<Thread, VectorPool>());

  public static VectorPool pool(RealVector prototype) {
    final Thread thread = Thread.currentThread();
    VectorPool pool = threadToPool.get(thread);
    if (pool == null) {
      pool = new ThreadVectorPool(prototype);
      threadToPool.put(thread, pool);
    }
    return pool;
  }
}
