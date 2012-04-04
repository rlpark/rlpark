package rlpark.plugin.rltoys.experiments.scheduling.queue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobQueue;
import rlpark.plugin.rltoys.experiments.scheduling.internal.JobDoneEventQueue;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;


public class LocalQueue implements JobQueue {
  static class JobInfo {
    final Runnable job;
    final Listener<JobDoneEvent> listener;

    JobInfo(Runnable job, Listener<JobDoneEvent> listener) {
      this.job = job;
      this.listener = listener;
    }
  }

  private final Map<Iterator<? extends Runnable>, Listener<JobDoneEvent>> listeners = new HashMap<Iterator<? extends Runnable>, Listener<JobDoneEvent>>();
  private final LinkedList<Iterator<? extends Runnable>> waiting = new LinkedList<Iterator<? extends Runnable>>();
  private final Map<Runnable, Listener<JobDoneEvent>> pending = new LinkedHashMap<Runnable, Listener<JobDoneEvent>>();
  private final LinkedList<JobInfo> canceled = new LinkedList<JobInfo>();
  private final JobDoneEventQueue jobDoneEventQueue = new JobDoneEventQueue();
  private final Random random = new Random(0);
  private Iterator<? extends Runnable> currentJobIterator = null;
  private int nbJobsDone = 0;
  private boolean poolFromPending = false;

  synchronized public void requestCancel(Runnable pendingJob) {
    if (!pending.containsKey(pendingJob))
      return;
    Listener<JobDoneEvent> listener = pending.remove(pendingJob);
    canceled.addFirst(new JobInfo(pendingJob, listener));
  }

  private JobInfo findJob() {
    JobInfo jobInfo = canceled.poll();
    while (jobInfo == null) {
      if (currentJobIterator == null)
        currentJobIterator = waiting.poll();
      if (currentJobIterator == null)
        break;
      if (currentJobIterator.hasNext()) {
        Runnable job = currentJobIterator.next();
        jobInfo = new JobInfo(job, listeners.get(currentJobIterator));
      }
      if (!currentJobIterator.hasNext()) {
        listeners.remove(currentJobIterator);
        currentJobIterator = null;
      }
    }
    return jobInfo;
  }

  public void enablePoolFromPending() {
    poolFromPending = true;
  }

  @Override
  synchronized public Runnable request() {
    JobInfo jobInfo = findJob();
    if (jobInfo == null)
      return findPendingJob();
    pending.put(jobInfo.job, jobInfo.listener);
    return jobInfo.job;
  }

  private Runnable findPendingJob() {
    if (!poolFromPending || pending.isEmpty())
      return null;
    Runnable[] jobs = new Runnable[pending.size()];
    pending.keySet().toArray(jobs);
    return Utils.choose(random, jobs);
  }

  @Override
  synchronized public void done(Runnable todo, Runnable done) {
    boolean removed = pending.containsKey(todo);
    if (!removed)
      return;
    Listener<JobDoneEvent> listener = pending.remove(todo);
    jobDoneEventQueue.onJobDone(new JobDoneEvent(todo, done), listener);
    nbJobsDone++;
  }

  synchronized public boolean areAllDone() {
    return currentJobIterator == null && waiting.isEmpty() && pending.isEmpty() && canceled.isEmpty()
        && jobDoneEventQueue.isEmpty();
  }

  synchronized public void add(Iterator<? extends Runnable> jobIterator, Listener<JobDoneEvent> listener) {
    listeners.put(jobIterator, listener);
    waiting.add(jobIterator);
  }

  static public void waitAllDone(LocalQueue localQueue) {
    final Semaphore semaphore = new Semaphore(0);
    final Listener<JobDoneEvent> listener = new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        semaphore.release();
      }
    };
    localQueue.onJobDone().connect(listener);
    while (!localQueue.areAllDone()) {
      try {
        semaphore.acquire();
      } catch (InterruptedException e) {
      }
    }
    localQueue.onJobDone().disconnect(listener);
  }

  @Override
  public Signal<JobDoneEvent> onJobDone() {
    return jobDoneEventQueue.onJobDone;
  }

  public int nbJobsDone() {
    return nbJobsDone;
  }

  @Override
  public void dispose() {
    jobDoneEventQueue.dispose();
  }
}
