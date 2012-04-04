package rlpark.plugin.rltoys.experiments.scheduling.pools;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import zephyr.plugin.core.api.signals.Listener;


public class MemoryJobPool extends AbstractJobPool {
  protected final List<Runnable> jobs = new ArrayList<Runnable>();

  public MemoryJobPool(JobPoolListener onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    super(onAllJobDone, onJobDone);
  }

  @Override
  public void add(Runnable job) {
    checkHasBeenSubmitted();
    jobs.add(job);
  }

  @Override
  protected Iterator<Runnable> createIterator() {
    return jobs.iterator();
  }
}
