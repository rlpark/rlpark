package rlpark.plugin.rltoys.experiments.scheduling.interfaces;

import zephyr.plugin.core.api.signals.Signal;

public interface JobQueue {
  Runnable request();

  void done(Runnable todo, Runnable done);

  Signal<JobDoneEvent> onJobDone();

  void dispose();
}
