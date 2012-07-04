package rlpark.example.demos.scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.queue.LocalQueue;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;


public class LocalScheduling {
  static Iterator<? extends Runnable> createJobList() {
    List<Runnable> jobs = new ArrayList<Runnable>();
    for (int i = 0; i < 100; i++)
      jobs.add(new Job());
    return jobs.iterator();
  }

  static Listener<JobDoneEvent> createJobDoneListener() {
    return new Listener<JobDoneEvent>() {

      @Override
      public void listen(JobDoneEvent eventInfo) {
        System.out.println("Job done. Result: " + ((Job) eventInfo.done).result());
      }
    };
  }

  public static void main(String[] args) {
    LocalScheduler scheduler = new LocalScheduler();
    Iterator<? extends Runnable> jobList = createJobList();
    Listener<JobDoneEvent> jobDoneListener = createJobDoneListener();
    ((LocalQueue) scheduler.queue()).add(jobList, jobDoneListener);
    scheduler.start();
    scheduler.waitAll();
  }
}
