package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rlpark.plugin.rltoys.math.vector.RealVector;

public class VectorPools {
  static public Map<Thread, ThreadVectorPool> threadToPool = Collections
      .synchronizedMap(new HashMap<Thread, ThreadVectorPool>());

  public static VectorPool pool(RealVector prototype) {
    final Thread thread = Thread.currentThread();
    ThreadVectorPool pool = threadToPool.get(thread);
    if (pool == null) {
      pool = new ThreadVectorPool();
      threadToPool.put(thread, pool);
    }
    pool.setPrototype(prototype);
    return pool;
  }
}
