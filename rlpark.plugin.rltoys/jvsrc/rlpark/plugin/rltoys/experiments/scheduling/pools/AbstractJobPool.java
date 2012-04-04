package rlpark.plugin.rltoys.experiments.scheduling.pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.PoolResult;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.queue.LocalQueue;
import zephyr.plugin.core.api.signals.Listener;


public abstract class AbstractJobPool implements JobPool {
  class RunnableIterator implements Iterator<Runnable> {
    private final Iterator<Runnable> iterator;

    RunnableIterator(Iterator<Runnable> iterator) {
      this.iterator = iterator;
    }

    @Override
    synchronized public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    synchronized public Runnable next() {
      if (nbRequestedJob == 0)
        onPoolStart();
      Runnable next = iterator.next();
      jobSubmitted.add(next);
      nbRequestedJob++;
      return next;
    }

    @Override
    public void remove() {
    }

    synchronized public boolean noRemainingJob() {
      return jobSubmitted.isEmpty() && !hasNext();
    }
  }

  protected final JobPoolListener onAllJobDone;
  private final Listener<JobDoneEvent> poolListener = new Listener<JobDoneEvent>() {
    @Override
    public void listen(JobDoneEvent eventInfo) {
      onJobDone(eventInfo);
    }
  };
  protected final Listener<JobDoneEvent> onJobDone;
  final List<Runnable> jobSubmitted = Collections.synchronizedList(new ArrayList<Runnable>());
  protected RunnableIterator jobIterator = null;
  protected int nbRequestedJob = 0;
  protected PoolResult poolResult = null;

  public AbstractJobPool(JobPoolListener onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    this.onAllJobDone = onAllJobDone;
    this.onJobDone = onJobDone;
  }

  protected void onPoolStart() {
  }

  protected void onPoolEnd() {
  }

  protected boolean hasBeenSubmitted() {
    return jobIterator != null && poolResult != null;
  }

  @Override
  public PoolResult submitTo(Scheduler scheduler) {
    checkHasBeenSubmitted();
    poolResult = new PoolResult();
    jobIterator = new RunnableIterator(createIterator());
    ((LocalQueue) scheduler.queue()).add(jobIterator, poolListener);
    return poolResult;
  }

  protected void checkHasBeenSubmitted() {
    if (hasBeenSubmitted())
      throw new RuntimeException("The pool has already been submitted");
  }

  protected void onJobDone(JobDoneEvent event) {
    assert jobSubmitted.contains(event.todo);
    if (onJobDone != null)
      onJobDone.listen(event);
    jobSubmitted.remove(event.todo);
    if (jobIterator.noRemainingJob()) {
      onAllJobDone.listen(AbstractJobPool.this);
      onPoolEnd();
      poolResult.poolDone();
    }
  }

  abstract protected Iterator<Runnable> createIterator();
}