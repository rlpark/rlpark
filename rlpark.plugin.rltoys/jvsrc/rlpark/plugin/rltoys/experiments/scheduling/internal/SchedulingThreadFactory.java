package rlpark.plugin.rltoys.experiments.scheduling.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulingThreadFactory {
  static private Map<String, Integer> poolPrefixNumber = new HashMap<String, Integer>();

  static synchronized int getPoolSuffix(String poolName) {
    Integer last = poolPrefixNumber.get(poolName);
    if (last == null)
      last = -1;
    int result = last + 1;
    poolPrefixNumber.put(poolName, result);
    return result;
  }

  static class NamedThreadFactory implements ThreadFactory {
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    NamedThreadFactory(String name) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      namePrefix = name + getPoolSuffix(name) + "-thread";
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      t.setDaemon(true);
      return t;
    }
  }

  public static int getDefaultNbThreads() {
    return Runtime.getRuntime().availableProcessors();
  }

  public static ExecutorService newFixedThreadPool(String name, int nThreads) {
    return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(name));
  }

}
