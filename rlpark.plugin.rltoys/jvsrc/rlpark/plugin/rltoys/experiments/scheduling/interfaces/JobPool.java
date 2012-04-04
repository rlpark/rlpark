package rlpark.plugin.rltoys.experiments.scheduling.interfaces;

import zephyr.plugin.core.api.signals.Listener;

public interface JobPool {
  interface JobPoolListener extends Listener<JobPool> {
  };

  PoolResult submitTo(Scheduler scheduler);

  void add(Runnable job);
}