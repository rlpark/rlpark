package rlpark.plugin.rltoys.math.vector.pool;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import rlpark.plugin.rltoys.math.vector.RealVector;

public class VectorPools {

  public static VectorPool pool(RealVector prototype) {
    final Thread thread = Thread.currentThread();
    VectorPool pool = VectorPools.threadToPool.get(thread);
    if (pool == null) {
      pool = new ThreadVectorPool(prototype);
      VectorPools.threadToPool.put(thread, new ThreadVectorPool(prototype));
    }
    return pool;
  }

  static public Map<Thread, VectorPool> threadToPool = Collections
  .synchronizedMap(new LinkedHashMap<Thread, VectorPool>());

}
