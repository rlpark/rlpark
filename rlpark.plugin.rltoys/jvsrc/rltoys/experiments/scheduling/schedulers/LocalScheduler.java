package rltoys.experiments.scheduling.schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.internal.SchedulingThreadFactory;
import rltoys.experiments.scheduling.queue.LocalQueue;
import zephyr.plugin.core.api.synchronization.Chrono;

public class LocalScheduler implements Scheduler {
  protected class RunnableProcessor implements Runnable {
    @Override
    public void run() {
      try {
        Runnable runnable = runnables.request();
        while (exceptionThrown == null && runnable != null) {
          runnable.run();
          runnables.done(runnable, runnable);
          runnable = runnables.request();
        }
      } catch (Throwable exception) {
        if (exceptionThrown == null)
          exceptionThrown = exception;
        exception.printStackTrace();
      }
    }
  }

  Throwable exceptionThrown = null;
  private final ExecutorService executor;
  private final List<RunnableProcessor> updaters = new ArrayList<RunnableProcessor>();
  private final Future<?>[] futurs;
  protected final JobQueue runnables;
  private final Chrono chrono = new Chrono();
  protected final int nbThread;

  public LocalScheduler() {
    this(getDefaultNbThreads() + 1);
  }

  public LocalScheduler(int nbThread) {
    this(nbThread, new LocalQueue());
  }

  public LocalScheduler(JobQueue runnables) {
    this(getDefaultNbThreads() + 1, runnables);
  }

  public static int getDefaultNbThreads() {
    return Runtime.getRuntime().availableProcessors();
  }

  public LocalScheduler(int nbThread, JobQueue runnables) {
    this.nbThread = nbThread;
    this.runnables = runnables;
    for (int i = 0; i < nbThread; i++)
      updaters.add(new RunnableProcessor());
    futurs = new Future<?>[nbThread];
    executor = SchedulingThreadFactory.newFixedThreadPool("LocalScheduler", nbThread);
  }

  synchronized public void start() {
    exceptionThrown = null;
    chrono.start();
    for (int i = 0; i < updaters.size(); i++)
      if (futurs[i] == null || futurs[i].isDone())
        futurs[i] = executor.submit(updaters.get(i));
  }

  @Override
  public void runAll() {
    start();
    for (Future<?> future : futurs)
      try {
        future.get();
      } catch (Exception e) {
        exceptionThrown = e;
        break;
      }
    if (exceptionThrown != null)
      throw new RuntimeException(exceptionThrown);
  }

  public long updateTimeAverage() {
    return chrono.getCurrentNano();
  }

  @Override
  synchronized public void dispose() {
    executor.shutdown();
  }

  public Chrono chrono() {
    return chrono;
  }

  @Override
  public JobQueue queue() {
    return runnables;
  }

  public Throwable exceptionOccured() {
    return exceptionThrown;
  }

  public boolean isShutdown() {
    return executor.isShutdown();
  }
}
